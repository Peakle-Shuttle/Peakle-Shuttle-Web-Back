package com.peakle.shuttle.admin.stats;

import com.peakle.shuttle.admin.stats.dto.response.PassengerStatsResponse;
import com.peakle.shuttle.admin.stats.dto.response.PerformanceStatsResponse;
import com.peakle.shuttle.admin.stats.dto.response.PerformanceStatsResponse.*;
import com.peakle.shuttle.admin.stats.dto.response.StatsDetailResponse;
import com.peakle.shuttle.admin.stats.dto.response.StatsSummaryResponse;
import com.peakle.shuttle.admin.stats.dto.response.StatsSummaryResponse.DailyStatEntry;
import com.peakle.shuttle.admin.stats.dto.response.StatsSummaryResponse.StatEntry;
import com.peakle.shuttle.admin.stats.enums.StatsInterval;
import com.peakle.shuttle.admin.visitor.repository.VisitorStatRepository;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.matching.repository.MatchingRepository;
import com.peakle.shuttle.qna.repository.QnaRepository;
import com.peakle.shuttle.reservation.entity.Reservation;
import com.peakle.shuttle.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatsService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final QnaRepository qnaRepository;
    private final MatchingRepository matchingRepository;
    private final VisitorStatRepository visitorStatRepository;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String UV_KEY_PREFIX = "visitor:uv:";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DAILY_FORMATTER = DateTimeFormatter.ofPattern("MM.dd");
    private static final DateTimeFormatter MONTHLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM");

    /**
     * 성과 분석 통합 조회
     */
    public PerformanceStatsResponse getPerformanceStats(
            LocalDate startDate, LocalDate endDate, StatsInterval interval) {

        // 공유 데이터 한 번에 조회
        List<Reservation> allReservations = reservationRepository.findAllValidReservationsWithUser();

        Map<Long, LocalDate> firstPurchaseDateByUser = reservationRepository.findFirstReservationDateByUser().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((LocalDateTime) row[1]).toLocalDate()
                ));

        Map<Long, Long> reservationCountByUser = reservationRepository.findAllUserReservationCounts().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        // 1. 재구매 매출 (Mock 데이터)
        RevenueData revenueData = RevenueData.builder()
                .totalRevenue(1500000L)
                .repeatRevenue(1200000L)
                .ratio(80.0)
                .build();

        // 2~5. 일별 기준으로 계산 후 interval에 따라 집계
        List<BuyerRatioEntry> dailyBuyerRatio = computeDailyBuyerRatio(
                allReservations, firstPurchaseDateByUser, startDate, endDate);
        List<ProbabilityEntry> dailyProbability = computeDailyProbability(
                firstPurchaseDateByUser, reservationCountByUser, startDate, endDate);
        List<CycleEntry> dailyCycle = computeDailyCycle(allReservations, startDate, endDate);
        List<LoyaltyEntry> dailyLoyalty = computeDailyLoyalty(allReservations, startDate, endDate);

        return PerformanceStatsResponse.builder()
                .repeatRevenue(revenueData)
                .repeatBuyerRatio(aggregate(dailyBuyerRatio, interval, startDate))
                .repurchaseProbability(aggregate(dailyProbability, interval, startDate))
                .repurchaseCycle(aggregate(dailyCycle, interval, startDate))
                .loyalCustomerDistribution(aggregate(dailyLoyalty, interval, startDate))
                .build();
    }

    /**
     * 기간별 탑승자 수 통계 조회
     */
    public List<PassengerStatsResponse> getPassengerStats(
            LocalDate startDate, LocalDate endDate, StatsInterval interval) {

        List<Object[]> raw = reservationRepository.findDailyPassengerCounts(startDate, endDate);

        Map<LocalDate, Long> dailyMap = raw.stream()
                .collect(Collectors.toMap(
                        row -> (LocalDate) row[0],
                        row -> (Long) row[1]
                ));

        List<PassengerStatsResponse> dailyStats = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> PassengerStatsResponse.of(
                        date.format(DAILY_FORMATTER),
                        dailyMap.getOrDefault(date, 0L)))
                .toList();

        if (interval == StatsInterval.DAILY) {
            return dailyStats;
        }

        Map<String, Long> grouped = new LinkedHashMap<>();
        LocalDate currentDate = startDate;
        for (PassengerStatsResponse stat : dailyStats) {
            String key = formatIntervalKey(currentDate, interval);
            grouped.merge(key, stat.passengerCount(), Long::sum);
            currentDate = currentDate.plusDays(1);
        }

        return grouped.entrySet().stream()
                .map(e -> PassengerStatsResponse.of(e.getKey(), e.getValue()))
                .toList();
    }

    /**
     * 통계 요약 조회 (선택 기간 일별 + 선택기간 합계)
     */
    public StatsSummaryResponse getStatsSummary(LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Long> orderMap = toDateMap(reservationRepository.findDailyOrderCounts(startDate, endDate));
        Map<LocalDate, Long> revenueMap = toDateMap(reservationRepository.findDailyRevenue(startDate, endDate));
        Map<LocalDate, Long> signupMap = toDateMap(userRepository.findDailySignupCounts(startDate, endDate));
        Map<LocalDate, Long> inquiryMap = toDateMap(qnaRepository.findDailyInquiryCounts(startDate, endDate));
        Map<LocalDate, Long> matchingMap = toDateMap(matchingRepository.findDailyMatchingCounts(startDate, endDate));
        Map<LocalDate, Long> visitMap = buildVisitMap(startDate, endDate);

        List<DailyStatEntry> dailyStats = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> new DailyStatEntry(
                        date.format(DAILY_FORMATTER),
                        orderMap.getOrDefault(date, 0L),
                        revenueMap.getOrDefault(date, 0L),
                        visitMap.getOrDefault(date, 0L),
                        signupMap.getOrDefault(date, 0L),
                        inquiryMap.getOrDefault(date, 0L),
                        matchingMap.getOrDefault(date, 0L)
                ))
                .toList();

        return new StatsSummaryResponse(
                dailyStats,
                sumPeriod(orderMap, revenueMap, visitMap, signupMap, inquiryMap, matchingMap, startDate, endDate)
        );
    }

    /**
     * 통계 상세 분석 조회 (일별/주별/월별)
     */
    public List<StatsDetailResponse> getDetailStats(
            LocalDate startDate, LocalDate endDate, StatsInterval interval) {

        Map<LocalDate, Long> orderMap = toDateMap(reservationRepository.findDailyOrderCounts(startDate, endDate));
        Map<LocalDate, Long> revenueMap = toDateMap(reservationRepository.findDailyRevenue(startDate, endDate));
        Map<LocalDate, Long> signupMap = toDateMap(userRepository.findDailySignupCounts(startDate, endDate));
        Map<LocalDate, Long> inquiryMap = toDateMap(qnaRepository.findDailyInquiryCounts(startDate, endDate));
        Map<LocalDate, Long> matchingMap = toDateMap(matchingRepository.findDailyMatchingCounts(startDate, endDate));
        Map<LocalDate, Long> visitMap = buildVisitMap(startDate, endDate);

        List<StatsDetailResponse> dailyStats = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> new StatsDetailResponse(
                        date.format(DAILY_FORMATTER),
                        orderMap.getOrDefault(date, 0L),
                        revenueMap.getOrDefault(date, 0L),
                        visitMap.getOrDefault(date, 0L),
                        signupMap.getOrDefault(date, 0L),
                        inquiryMap.getOrDefault(date, 0L),
                        matchingMap.getOrDefault(date, 0L)
                ))
                .toList();

        if (interval == StatsInterval.DAILY) {
            return dailyStats;
        }

        Map<String, long[]> grouped = new LinkedHashMap<>();
        LocalDate currentDate = startDate;
        for (StatsDetailResponse stat : dailyStats) {
            String key = formatIntervalKey(currentDate, interval);
            long[] sums = grouped.computeIfAbsent(key, k -> new long[6]);
            sums[0] += stat.orders();
            sums[1] += stat.revenue();
            sums[2] += stat.visits();
            sums[3] += stat.signups();
            sums[4] += stat.inquiries();
            sums[5] += stat.matchings();
            currentDate = currentDate.plusDays(1);
        }

        return grouped.entrySet().stream()
                .map(e -> new StatsDetailResponse(
                        e.getKey(), e.getValue()[0], e.getValue()[1], e.getValue()[2],
                        e.getValue()[3], e.getValue()[4], e.getValue()[5]))
                .toList();
    }

    private Map<LocalDate, Long> toDateMap(List<Object[]> rows) {
        Map<LocalDate, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            LocalDate date = (LocalDate) row[0];
            Long value = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            map.put(date, value);
        }
        return map;
    }

    private Map<LocalDate, Long> buildVisitMap(LocalDate start, LocalDate end) {
        Map<LocalDate, Long> visitMap = new HashMap<>();

        // Redis에서 최근 7일 UV 조회
        LocalDate today = LocalDate.now();
        LocalDate redisStart = today.minusDays(6);
        for (LocalDate date = redisStart; !date.isAfter(today); date = date.plusDays(1)) {
            String uvKey = UV_KEY_PREFIX + date.format(DATE_FORMAT);
            Long uv = stringRedisTemplate.opsForSet().size(uvKey);
            visitMap.put(date, uv != null ? uv : 0L);
        }

        // DB에서 Redis 범위 이전 데이터 조회
        LocalDate dbEnd = redisStart.minusDays(1);
        if (!start.isAfter(dbEnd)) {
            visitorStatRepository.findByStatDateBetweenOrderByStatDateDesc(start, dbEnd)
                    .forEach(stat -> visitMap.put(stat.getStatDate(), stat.getUniqueVisitors()));
        }

        return visitMap;
    }

    private StatEntry sumPeriod(
            Map<LocalDate, Long> orders, Map<LocalDate, Long> revenue,
            Map<LocalDate, Long> visits, Map<LocalDate, Long> signups,
            Map<LocalDate, Long> inquiries, Map<LocalDate, Long> matchings,
            LocalDate start, LocalDate end) {

        long totalOrders = 0, totalRevenue = 0, totalVisits = 0;
        long totalSignups = 0, totalInquiries = 0, totalMatchings = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            totalOrders += orders.getOrDefault(date, 0L);
            totalRevenue += revenue.getOrDefault(date, 0L);
            totalVisits += visits.getOrDefault(date, 0L);
            totalSignups += signups.getOrDefault(date, 0L);
            totalInquiries += inquiries.getOrDefault(date, 0L);
            totalMatchings += matchings.getOrDefault(date, 0L);
        }

        return new StatEntry(totalOrders, totalRevenue, totalVisits, totalSignups, totalInquiries, totalMatchings);
    }

    // ===== 일별 계산 로직 =====

    private List<BuyerRatioEntry> computeDailyBuyerRatio(
            List<Reservation> allReservations,
            Map<Long, LocalDate> firstPurchaseDateByUser,
            LocalDate startDate, LocalDate endDate) {

        Map<LocalDate, Set<Long>> usersByDate = allReservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().toLocalDate(),
                        Collectors.mapping(r -> r.getUser().getUserCode(), Collectors.toSet())
                ));

        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> {
                    Set<Long> usersOnDate = usersByDate.getOrDefault(date, Collections.emptySet());
                    if (usersOnDate.isEmpty()) {
                        return BuyerRatioEntry.builder().date(date.format(DAILY_FORMATTER)).first(0).re(0).build();
                    }
                    long firstCount = usersOnDate.stream()
                            .filter(userCode -> date.equals(firstPurchaseDateByUser.get(userCode)))
                            .count();
                    int total = usersOnDate.size();
                    int firstPct = (int) Math.round(firstCount * 100.0 / total);
                    return BuyerRatioEntry.builder()
                            .date(date.format(DAILY_FORMATTER)).first(firstPct).re(100 - firstPct).build();
                })
                .toList();
    }

    private List<ProbabilityEntry> computeDailyProbability(
            Map<Long, LocalDate> firstPurchaseDateByUser,
            Map<Long, Long> reservationCountByUser,
            LocalDate startDate, LocalDate endDate) {

        Set<Long> usersWhoRepurchased = reservationCountByUser.entrySet().stream()
                .filter(e -> e.getValue() >= 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        List<Map.Entry<Long, LocalDate>> sortedEntries = firstPurchaseDateByUser.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();

        List<ProbabilityEntry> data = new ArrayList<>();
        int cumulativeTotal = 0;
        int cumulativeRepurchased = 0;
        int entryIdx = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            while (entryIdx < sortedEntries.size() && !sortedEntries.get(entryIdx).getValue().isAfter(date)) {
                cumulativeTotal++;
                if (usersWhoRepurchased.contains(sortedEntries.get(entryIdx).getKey())) {
                    cumulativeRepurchased++;
                }
                entryIdx++;
            }
            int probability = cumulativeTotal > 0
                    ? (int) Math.round(cumulativeRepurchased * 100.0 / cumulativeTotal) : 0;
            data.add(ProbabilityEntry.builder()
                    .date(date.format(DAILY_FORMATTER)).probability(probability).build());
        }

        return data;
    }

    private List<CycleEntry> computeDailyCycle(
            List<Reservation> allReservations, LocalDate startDate, LocalDate endDate) {

        Map<Long, List<Reservation>> reservationsByUser = allReservations.stream()
                .collect(Collectors.groupingBy(r -> r.getUser().getUserCode()));

        Map<LocalDate, List<Long>> gapsByDate = new HashMap<>();
        for (List<Reservation> userReservations : reservationsByUser.values()) {
            List<Reservation> sorted = userReservations.stream()
                    .sorted(Comparator.comparing(Reservation::getCreatedAt))
                    .toList();
            for (int i = 1; i < sorted.size(); i++) {
                long gap = ChronoUnit.DAYS.between(
                        sorted.get(i - 1).getCreatedAt(), sorted.get(i).getCreatedAt());
                LocalDate gapDate = sorted.get(i).getCreatedAt().toLocalDate();
                gapsByDate.computeIfAbsent(gapDate, k -> new ArrayList<>()).add(gap);
            }
        }

        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> {
                    List<Long> gaps = gapsByDate.getOrDefault(date, Collections.emptyList());
                    int avg = gaps.isEmpty() ? 0
                            : (int) Math.round(gaps.stream().mapToLong(Long::longValue).average().orElse(0));
                    return CycleEntry.builder().date(date.format(DAILY_FORMATTER)).averageDays(avg).build();
                })
                .toList();
    }

    private List<LoyaltyEntry> computeDailyLoyalty(
            List<Reservation> allReservations, LocalDate startDate, LocalDate endDate) {

        Map<Long, List<LocalDate>> reservationDatesByUser = allReservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getUser().getUserCode(),
                        Collectors.mapping(r -> r.getCreatedAt().toLocalDate(), Collectors.toList())
                ));

        Map<LocalDate, List<Long>> eventsByDate = new TreeMap<>();
        for (Map.Entry<Long, List<LocalDate>> entry : reservationDatesByUser.entrySet()) {
            for (LocalDate date : entry.getValue()) {
                eventsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(entry.getKey());
            }
        }

        Map<Long, Integer> userCumulativeCount = new HashMap<>();
        int[] levelCounts = new int[6]; // index 1-5

        // startDate 이전의 이벤트 처리 (초기 상태 구성)
        for (Map.Entry<LocalDate, List<Long>> event : eventsByDate.entrySet()) {
            if (!event.getKey().isBefore(startDate)) break;
            for (Long userCode : event.getValue()) {
                int oldCount = userCumulativeCount.getOrDefault(userCode, 0);
                int newCount = oldCount + 1;
                userCumulativeCount.put(userCode, newCount);
                if (oldCount > 0) levelCounts[levelIndex(oldCount)]--;
                levelCounts[levelIndex(newCount)]++;
            }
        }

        List<LoyaltyEntry> data = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Long> dayEvents = eventsByDate.getOrDefault(date, Collections.emptyList());
            for (Long userCode : dayEvents) {
                int oldCount = userCumulativeCount.getOrDefault(userCode, 0);
                int newCount = oldCount + 1;
                userCumulativeCount.put(userCode, newCount);
                if (oldCount > 0) levelCounts[levelIndex(oldCount)]--;
                levelCounts[levelIndex(newCount)]++;
            }

            int totalUsers = Arrays.stream(levelCounts, 1, 6).sum();
            if (totalUsers == 0) {
                data.add(LoyaltyEntry.builder()
                        .date(date.format(DAILY_FORMATTER))
                        .lv1(0).lv2(0).lv3(0).lv4(0).lv5(0).build());
            } else {
                int lv1Pct = (int) Math.round(levelCounts[1] * 100.0 / totalUsers);
                int lv2Pct = (int) Math.round(levelCounts[2] * 100.0 / totalUsers);
                int lv3Pct = (int) Math.round(levelCounts[3] * 100.0 / totalUsers);
                int lv4Pct = (int) Math.round(levelCounts[4] * 100.0 / totalUsers);
                int lv5Pct = 100 - lv1Pct - lv2Pct - lv3Pct - lv4Pct;
                data.add(LoyaltyEntry.builder()
                        .date(date.format(DAILY_FORMATTER))
                        .lv1(lv1Pct).lv2(lv2Pct).lv3(lv3Pct).lv4(lv4Pct).lv5(lv5Pct).build());
            }
        }

        return data;
    }

    // ===== Interval 집계 =====

    @SuppressWarnings("unchecked")
    private <T> List<T> aggregate(List<T> dailyData, StatsInterval interval, LocalDate startDate) {
        if (interval == StatsInterval.DAILY) {
            return dailyData;
        }

        if (dailyData.isEmpty()) {
            return dailyData;
        }

        T first = dailyData.get(0);
        if (first instanceof BuyerRatioEntry) {
            return (List<T>) aggregateBuyerRatio((List<BuyerRatioEntry>) dailyData, interval, startDate);
        } else if (first instanceof ProbabilityEntry) {
            return (List<T>) aggregateProbability((List<ProbabilityEntry>) dailyData, interval, startDate);
        } else if (first instanceof CycleEntry) {
            return (List<T>) aggregateCycle((List<CycleEntry>) dailyData, interval, startDate);
        } else if (first instanceof LoyaltyEntry) {
            return (List<T>) aggregateLoyalty((List<LoyaltyEntry>) dailyData, interval, startDate);
        }
        return dailyData;
    }

    private List<BuyerRatioEntry> aggregateBuyerRatio(
            List<BuyerRatioEntry> daily, StatsInterval interval, LocalDate startDate) {
        Map<String, List<BuyerRatioEntry>> grouped = groupByInterval(daily, interval, startDate);
        return grouped.entrySet().stream()
                .map(entry -> {
                    List<BuyerRatioEntry> items = entry.getValue();
                    // 기간 내 평균
                    int avgFirst = (int) Math.round(items.stream().mapToInt(BuyerRatioEntry::getFirst).average().orElse(0));
                    return BuyerRatioEntry.builder()
                            .date(entry.getKey()).first(avgFirst).re(100 - avgFirst).build();
                })
                .toList();
    }

    private List<ProbabilityEntry> aggregateProbability(
            List<ProbabilityEntry> daily, StatsInterval interval, LocalDate startDate) {
        Map<String, List<ProbabilityEntry>> grouped = groupByInterval(daily, interval, startDate);
        return grouped.entrySet().stream()
                .map(entry -> {
                    // 기간 마지막 날의 누적값
                    List<ProbabilityEntry> items = entry.getValue();
                    ProbabilityEntry last = items.get(items.size() - 1);
                    return ProbabilityEntry.builder()
                            .date(entry.getKey()).probability(last.getProbability()).build();
                })
                .toList();
    }

    private List<CycleEntry> aggregateCycle(
            List<CycleEntry> daily, StatsInterval interval, LocalDate startDate) {
        Map<String, List<CycleEntry>> grouped = groupByInterval(daily, interval, startDate);
        return grouped.entrySet().stream()
                .map(entry -> {
                    // 기간 내 평균
                    List<CycleEntry> items = entry.getValue();
                    List<CycleEntry> nonZero = items.stream()
                            .filter(e -> e.getAverageDays() > 0).toList();
                    int avg = nonZero.isEmpty() ? 0
                            : (int) Math.round(nonZero.stream().mapToInt(CycleEntry::getAverageDays).average().orElse(0));
                    return CycleEntry.builder().date(entry.getKey()).averageDays(avg).build();
                })
                .toList();
    }

    private List<LoyaltyEntry> aggregateLoyalty(
            List<LoyaltyEntry> daily, StatsInterval interval, LocalDate startDate) {
        Map<String, List<LoyaltyEntry>> grouped = groupByInterval(daily, interval, startDate);
        return grouped.entrySet().stream()
                .map(entry -> {
                    // 기간 마지막 날의 누적값
                    List<LoyaltyEntry> items = entry.getValue();
                    LoyaltyEntry last = items.get(items.size() - 1);
                    return LoyaltyEntry.builder()
                            .date(entry.getKey())
                            .lv1(last.getLv1()).lv2(last.getLv2()).lv3(last.getLv3())
                            .lv4(last.getLv4()).lv5(last.getLv5()).build();
                })
                .toList();
    }

    private <T> Map<String, List<T>> groupByInterval(
            List<T> dailyData, StatsInterval interval, LocalDate startDate) {
        Map<String, List<T>> grouped = new LinkedHashMap<>();
        LocalDate currentDate = startDate;

        for (int i = 0; i < dailyData.size(); i++) {
            String key = formatIntervalKey(currentDate, interval);
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(dailyData.get(i));
            currentDate = currentDate.plusDays(1);
        }

        return grouped;
    }

    private String formatIntervalKey(LocalDate date, StatsInterval interval) {
        return switch (interval) {
            case DAILY -> date.format(DAILY_FORMATTER);
            case WEEKLY -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).format(DAILY_FORMATTER);
            case MONTHLY -> date.format(MONTHLY_FORMATTER);
        };
    }

    // ===== Private Helpers =====

    private int levelIndex(int count) {
        return Math.min(count, 5);
    }
}
