package com.peakle.shuttle.admin.visitor;

import com.peakle.shuttle.admin.visitor.dto.response.VisitorStatResponse;
import com.peakle.shuttle.admin.visitor.entity.VisitorStat;
import com.peakle.shuttle.admin.visitor.repository.VisitorStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorStatService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final VisitorStatRepository visitorStatRepository;

    private static final String UV_KEY_PREFIX = "visitor:uv:";
    private static final String PV_KEY_PREFIX = "visitor:pv:";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 방문자 기록 (UV, PV 증가)
     *
     * @param ip 방문자 IP 주소
     */
    public void recordVisit(String ip) {
        String today = LocalDate.now().format(DATE_FORMAT);
        String uvKey = UV_KEY_PREFIX + today;
        String pvKey = PV_KEY_PREFIX + today;

        // UV: Set에 IP 추가 (중복 자동 제거)
        redisTemplate.opsForSet().add(uvKey, ip);

        // PV: 카운터 증가
        redisTemplate.opsForValue().increment(pvKey);
    }

    /**
     * 오늘의 방문자 통계 조회
     *
     * @return 오늘의 UV, PV
     */
    public VisitorStatResponse getTodayStat() {
        String today = LocalDate.now().format(DATE_FORMAT);
        return getStatByDate(today);
    }

    /**
     * 특정 날짜의 방문자 통계 조회
     *
     * @param date 조회할 날짜 (yyyy-MM-dd)
     * @return 해당 날짜의 UV, PV
     */
    public VisitorStatResponse getStatByDate(String date) {
        String uvKey = UV_KEY_PREFIX + date;
        String pvKey = PV_KEY_PREFIX + date;

        Long uv = redisTemplate.opsForSet().size(uvKey);
        Object pvObj = redisTemplate.opsForValue().get(pvKey);
        Long pv = pvObj != null ? Long.parseLong(pvObj.toString()) : 0L;

        return VisitorStatResponse.of(date, uv != null ? uv : 0L, pv);
    }

    /**
     * 최근 N일간의 방문자 통계 조회
     *
     * @param days 조회할 일수
     * @return 일별 UV, PV 목록
     */
    public List<VisitorStatResponse> getRecentStats(int days) {
        List<VisitorStatResponse> stats = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < days; i++) {
            String date = today.minusDays(i).format(DATE_FORMAT);
            stats.add(getStatByDate(date));
        }

        return stats;
    }

    /**
     * 매일 자정에 전날 통계를 MySQL에 저장
     * Redis 데이터는 7일 후 자동 만료되므로 영구 보관용
     */
    @Scheduled(cron = "0 5 0 * * *") // 매일 00:05 실행
    @Transactional
    public void persistYesterdayStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String dateStr = yesterday.format(DATE_FORMAT);

        VisitorStatResponse stat = getStatByDate(dateStr);

        visitorStatRepository.findByStatDate(yesterday)
                .ifPresentOrElse(
                        existing -> existing.updateStats(stat.getUniqueVisitors(), stat.getPageViews()),
                        () -> visitorStatRepository.save(VisitorStat.builder()
                                .statDate(yesterday)
                                .uniqueVisitors(stat.getUniqueVisitors())
                                .pageViews(stat.getPageViews())
                                .build())
                );

        log.info("방문자 통계 저장 완료: {} - UV: {}, PV: {}",
                dateStr, stat.getUniqueVisitors(), stat.getPageViews());
    }

    /**
     * MySQL에서 과거 통계 조회 (Redis 만료 후 사용)
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 일별 UV, PV 목록
     */
    @Transactional(readOnly = true)
    public List<VisitorStatResponse> getStatsFromDB(LocalDate startDate, LocalDate endDate) {
        return visitorStatRepository.findByStatDateBetweenOrderByStatDateDesc(startDate, endDate)
                .stream()
                .map(stat -> VisitorStatResponse.of(
                        stat.getStatDate().format(DATE_FORMAT),
                        stat.getUniqueVisitors(),
                        stat.getPageViews()))
                .toList();
    }
}
