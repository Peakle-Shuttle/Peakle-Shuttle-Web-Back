package com.peakle.shuttle.admin.visitor;

import com.peakle.shuttle.admin.stats.enums.StatsInterval;
import com.peakle.shuttle.admin.visitor.dto.response.VisitorStatResponse;
import com.peakle.shuttle.admin.visitor.entity.VisitorStat;
import com.peakle.shuttle.admin.visitor.repository.VisitorStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorStatService {

    private final StringRedisTemplate stringRedisTemplate;
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

        try {
            // UV: Set에 IP 추가 (중복 자동 제거)
            stringRedisTemplate.opsForSet().add(uvKey, ip);

            // PV: 카운터 증가
            stringRedisTemplate.opsForValue().increment(pvKey);

            log.info("방문자 기록 성공 - IP: {}, UV Key: {}, PV Key: {}", ip, uvKey, pvKey);
        } catch (Exception e) {
            log.error("방문자 기록 실패 - IP: {}, Redis 에러: {}", ip, e.getMessage(), e);
        }
    }

    /**
     * 최근 N일간의 방문자 통계 조회 (Redis)
     *
     * @param days 조회할 일수
     * @return 일별 UV, PV 목록
     */
    public List<VisitorStatResponse> getRecentStats(int days) {
        List<VisitorStatResponse> stats = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < days; i++) {
            String date = today.minusDays(i).format(DATE_FORMAT);
            String uvKey = UV_KEY_PREFIX + date;
            String pvKey = PV_KEY_PREFIX + date;

            Long uv = stringRedisTemplate.opsForSet().size(uvKey);
            Object pvObj = stringRedisTemplate.opsForValue().get(pvKey);
            Long pv = pvObj != null ? Long.parseLong(pvObj.toString()) : 0L;

            stats.add(VisitorStatResponse.of(date, uv != null ? uv : 0L, pv));
        }

        return stats;
    }

    /**
     * 기간별 방문자 통계 조회 (MySQL)
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param interval 집계 단위 (DAILY, WEEKLY, MONTHLY)
     * @return 집계된 UV, PV 목록
     */
    @Transactional(readOnly = true)
    public List<VisitorStatResponse> getStatsByPeriod(LocalDate startDate, LocalDate endDate, StatsInterval interval) {
        List<VisitorStatResponse> dailyStats = getStatsFromDB(startDate, endDate);

        return switch (interval) {
            case DAILY -> dailyStats;
            case WEEKLY -> aggregateByWeek(dailyStats);
            case MONTHLY -> aggregateByMonth(dailyStats);
        };
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

        String uvKey = UV_KEY_PREFIX + dateStr;
        String pvKey = PV_KEY_PREFIX + dateStr;

        Long uv = stringRedisTemplate.opsForSet().size(uvKey);
        Object pvObj = stringRedisTemplate.opsForValue().get(pvKey);
        Long pv = pvObj != null ? Long.parseLong(pvObj.toString()) : 0L;
        Long finalUv = uv != null ? uv : 0L;

        visitorStatRepository.findByStatDate(yesterday)
                .ifPresentOrElse(
                        existing -> existing.updateStats(finalUv, pv),
                        () -> visitorStatRepository.save(VisitorStat.builder()
                                .statDate(yesterday)
                                .uniqueVisitors(finalUv)
                                .pageViews(pv)
                                .build())
                );

        log.info("방문자 통계 저장 완료: {} - UV: {}, PV: {}", dateStr, finalUv, pv);
    }

    /**
     * MySQL에서 과거 통계 조회
     */
    private List<VisitorStatResponse> getStatsFromDB(LocalDate startDate, LocalDate endDate) {
        return visitorStatRepository.findByStatDateBetweenOrderByStatDateDesc(startDate, endDate)
                .stream()
                .map(stat -> VisitorStatResponse.of(
                        stat.getStatDate().format(DATE_FORMAT),
                        stat.getUniqueVisitors(),
                        stat.getPageViews()))
                .toList();
    }

    private List<VisitorStatResponse> aggregateByWeek(List<VisitorStatResponse> dailyStats) {
        Map<String, long[]> grouped = new LinkedHashMap<>();

        for (VisitorStatResponse stat : dailyStats) {
            LocalDate date = LocalDate.parse(stat.getDate(), DATE_FORMAT);
            LocalDate weekStart = date.with(WeekFields.of(Locale.KOREA).dayOfWeek(), 1);
            String key = weekStart.format(DATE_FORMAT);

            grouped.computeIfAbsent(key, k -> new long[2]);
            grouped.get(key)[0] += stat.getUniqueVisitors();
            grouped.get(key)[1] += stat.getPageViews();
        }

        return grouped.entrySet().stream()
                .map(e -> VisitorStatResponse.of(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .toList();
    }

    private List<VisitorStatResponse> aggregateByMonth(List<VisitorStatResponse> dailyStats) {
        Map<String, long[]> grouped = new LinkedHashMap<>();

        for (VisitorStatResponse stat : dailyStats) {
            LocalDate date = LocalDate.parse(stat.getDate(), DATE_FORMAT);
            String key = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            grouped.computeIfAbsent(key, k -> new long[2]);
            grouped.get(key)[0] += stat.getUniqueVisitors();
            grouped.get(key)[1] += stat.getPageViews();
        }

        return grouped.entrySet().stream()
                .map(e -> VisitorStatResponse.of(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .toList();
    }
}
