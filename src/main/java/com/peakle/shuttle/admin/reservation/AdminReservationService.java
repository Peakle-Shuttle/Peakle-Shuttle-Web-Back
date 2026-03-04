package com.peakle.shuttle.admin.reservation;

import com.peakle.shuttle.admin.reservation.dto.request.ReservationUpdateRequest;
import com.peakle.shuttle.admin.reservation.dto.response.*;
import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.course.entity.Dispatch;
import com.peakle.shuttle.course.repository.DispatchRepository;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.reservation.entity.Reservation;
import com.peakle.shuttle.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReservationService {

    private final ReservationRepository reservationRepository;
    private final DispatchRepository dispatchRepository;
    private final UserRepository userRepository;

    /**
     * 전체 예약 목록을 조회합니다.
     *
     * @return 예약 목록
     */
    public List<AdminReservationResponse> getAllReservations() {
        return reservationRepository.findAllWithDetails().stream()
                .map(AdminReservationResponse::from)
                .toList();
    }

    /**
     * 특정 사용자의 예약 목록을 조회합니다.
     *
     * @param userCode 사용자 코드
     * @return 예약 목록
     */
    public List<AdminReservationResponse> getReservationsByUser(Long userCode) {
        return reservationRepository.findAllByUserCodeWithDetails(userCode).stream()
                .map(AdminReservationResponse::from)
                .toList();
    }

    /**
     * 특정 예약의 상세 정보를 조회합니다. (예약 정보 + 사용자 정보)
     *
     * @param reservationCode 조회할 예약 코드
     * @return 예약 상세 정보 (예약 + 사용자)
     * @throws AuthException 예약을 찾을 수 없는 경우
     */
    public AdminReservationDetailResponse getReservationDetail(Long reservationCode) {
        Reservation reservation = reservationRepository.findByReservationCodeWithFullDetails(reservationCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_RESERVATION));
        return AdminReservationDetailResponse.from(reservation);
    }

    /**
     * 예약 정보를 수정합니다.
     *
     * @param reservationCode 수정할 예약 코드
     * @param request 예약 수정 요청 정보
     * @throws AuthException 예약을 찾을 수 없는 경우
     */
    @Transactional
    public void updateReservation(Long reservationCode, ReservationUpdateRequest request) {
        Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_RESERVATION));

        Dispatch dispatch = dispatchRepository.findByDispatchCodeForUpdate(
                reservation.getDispatch().getDispatchCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_DISPATCH));

        Integer totalSeats = dispatch.getCourse().getCourseSeats();
        Integer currentOccupied = dispatch.getDispatchOccupied() != null ? dispatch.getDispatchOccupied() : 0;
        Integer oldCount = reservation.getReservationCount();
        Integer diff = request.reservationCount() - oldCount;

        if (diff > 0 && (totalSeats - currentOccupied) < diff) {
            throw new AuthException(ExceptionCode.NOT_ENOUGH_SEATS);
        }

        reservation.updateReservationCount(request.reservationCount());
        if (diff > 0) {
            dispatch.incrementOccupied(diff);
        } else if (diff < 0) {
            dispatch.decrementOccupied(-diff);
        }
    }

    /**
     * 예약을 삭제합니다.
     *
     * @param reservationCode 삭제할 예약 코드
     * @throws AuthException 예약을 찾을 수 없는 경우
     */
    @Transactional
    public void deleteReservation(Long reservationCode) {
        Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_RESERVATION));

        Dispatch dispatch = dispatchRepository.findByDispatchCodeForUpdate(
                reservation.getDispatch().getDispatchCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_DISPATCH));

        dispatch.decrementOccupied(reservation.getReservationCount());
        reservationRepository.delete(reservation);
    }

    /**
     * 충성 고객 목록을 조회합니다. (2회 이상 예약한 사용자)
     *
     * @return 충성 고객 목록
     */
    public List<LoyalCustomerResponse> getLoyalCustomers() {
        List<Object[]> stats = reservationRepository.findLoyalCustomerStats();
        List<Long> userCodes = stats.stream()
                .map(row -> (Long) row[0])
                .toList();

        Map<Long, User> userMap = userRepository.findAllById(userCodes).stream()
                .collect(Collectors.toMap(User::getUserCode, user -> user));

        return stats.stream()
                .map(row -> {
                    Long userCode = (Long) row[0];
                    Long count = (Long) row[1];
                    User user = userMap.get(userCode);
                    String userName = user != null ? user.getUserName() : "Unknown";
                    return LoyalCustomerResponse.of(userCode, userName, count);
                })
                .toList();
    }

    /**
     * 재구매자 상세 목록을 조회합니다. (2회 이상 예약한 사용자와 예약 내역)
     *
     * @return 재구매자 목록
     */
    public List<RepeatPurchaserResponse> getRepeatPurchasers() {
        List<Object[]> stats = reservationRepository.findLoyalCustomerStats();
        List<Long> userCodes = stats.stream()
                .map(row -> (Long) row[0])
                .toList();

        if (userCodes.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> countMap = stats.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        List<Reservation> reservations = reservationRepository.findAllByUserCodesWithDetails(userCodes);

        Map<Long, List<Reservation>> reservationsByUser = reservations.stream()
                .collect(Collectors.groupingBy(r -> r.getUser().getUserCode()));

        return userCodes.stream()
                .map(userCode -> {
                    List<Reservation> userReservations = reservationsByUser.getOrDefault(userCode, Collections.emptyList());
                    if (userReservations.isEmpty()) {
                        return null;
                    }
                    User user = userReservations.get(0).getUser();
                    List<RepeatPurchaserResponse.ReservationSummary> summaries = userReservations.stream()
                            .map(r -> RepeatPurchaserResponse.ReservationSummary.builder()
                                    .reservationCode(r.getReservationCode())
                                    .dispatchDatetime(r.getDispatch().getDispatchDatetime())
                                    .reservationCount(r.getReservationCount())
                                    .createdAt(r.getCreatedAt())
                                    .build())
                            .toList();

                    return RepeatPurchaserResponse.builder()
                            .userCode(userCode)
                            .userName(user.getUserName())
                            .email(user.getUserEmail())
                            .reservationCount(countMap.get(userCode))
                            .reservations(summaries)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * 재구매자 매출을 조회합니다. (Mock 데이터)
     *
     * @return 재구매자 매출 정보
     */
    public RepeatPurchaserRevenueResponse getRepeatPurchaserRevenue() {
        return RepeatPurchaserRevenueResponse.mock();
    }

    /**
     * 재구매자 비율을 조회합니다.
     *
     * @return 재구매자 비율 정보
     */
    public RepeatPurchaserRatioResponse getRepeatPurchaserRatio() {
        Long totalUsers = reservationRepository.countDistinctUsers();
        Long repeatPurchasers = (long) reservationRepository.findLoyalCustomerStats().size();
        return RepeatPurchaserRatioResponse.of(totalUsers, repeatPurchasers);
    }

    /**
     * 재구매자의 구매 주기를 조회합니다. (가장 최근 예약 기준)
     *
     * @return 구매 주기 정보 목록
     */
    public List<PurchaseCycleResponse> getPurchaseCycle() {
        List<Object[]> stats = reservationRepository.findLoyalCustomerStats();
        List<Long> userCodes = stats.stream()
                .map(row -> (Long) row[0])
                .toList();

        if (userCodes.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> countMap = stats.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        List<Reservation> reservations = reservationRepository.findAllByUserCodesWithDetails(userCodes);

        Map<Long, Reservation> latestReservationByUser = reservations.stream()
                .collect(Collectors.toMap(
                        r -> r.getUser().getUserCode(),
                        r -> r,
                        (r1, r2) -> r1.getCreatedAt().isAfter(r2.getCreatedAt()) ? r1 : r2
                ));

        LocalDateTime now = LocalDateTime.now();

        return userCodes.stream()
                .map(userCode -> {
                    Reservation latest = latestReservationByUser.get(userCode);
                    if (latest == null) {
                        return null;
                    }
                    long daysSince = ChronoUnit.DAYS.between(latest.getCreatedAt(), now);
                    return PurchaseCycleResponse.builder()
                            .userCode(userCode)
                            .userName(latest.getUser().getUserName())
                            .lastReservationDate(latest.getCreatedAt())
                            .daysSinceLastReservation(daysSince)
                            .totalReservationCount(countMap.get(userCode))
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    // ===== 그래프용 집계 API =====

    /**
     * 재구매자 빈도 분포를 조회합니다. (2회, 3회, 4회, 5회+ 각각 몇 명인지)
     */
    public RepeatPurchaserFrequencyGraphResponse getRepeatPurchaserFrequencyGraph() {
        List<Object[]> allCounts = reservationRepository.findAllUserReservationCounts();

        Map<Long, Long> frequencyMap = allCounts.stream()
                .filter(row -> (Long) row[1] >= 2)
                .collect(Collectors.groupingBy(
                        row -> Math.min((Long) row[1], 5L),
                        Collectors.counting()
                ));

        List<RepeatPurchaserFrequencyGraphResponse.FrequencyBucket> distribution =
                LongStream.rangeClosed(2, 5)
                        .mapToObj(count -> RepeatPurchaserFrequencyGraphResponse.FrequencyBucket.builder()
                                .purchaseCount(count == 5 ? "5+" : String.valueOf(count))
                                .userCount(frequencyMap.getOrDefault(count, 0L))
                                .build())
                        .toList();

        long totalRepeat = distribution.stream()
                .mapToLong(RepeatPurchaserFrequencyGraphResponse.FrequencyBucket::getUserCount)
                .sum();

        return RepeatPurchaserFrequencyGraphResponse.builder()
                .distribution(distribution)
                .totalRepeatPurchasers(totalRepeat)
                .build();
    }

    /**
     * 월별 재구매자 수 추이를 조회합니다.
     */
    public RepeatPurchaserMonthlyTrendGraphResponse getRepeatPurchaserMonthlyTrendGraph() {
        List<Object[]> stats = reservationRepository.findLoyalCustomerStats();
        List<Long> repeatUserCodes = stats.stream()
                .map(row -> (Long) row[0])
                .toList();

        if (repeatUserCodes.isEmpty()) {
            return RepeatPurchaserMonthlyTrendGraphResponse.builder()
                    .trend(Collections.emptyList())
                    .build();
        }

        List<Reservation> reservations = reservationRepository.findAllByUserCodesWithDetails(repeatUserCodes);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

        Map<String, Long> monthlyCountMap = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().format(monthFormatter),
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.mapping(r -> r.getUser().getUserCode(), Collectors.toSet()),
                                set -> (long) set.size()
                        )
                ));

        List<RepeatPurchaserMonthlyTrendGraphResponse.MonthlyDataPoint> trend =
                monthlyCountMap.entrySet().stream()
                        .map(entry -> RepeatPurchaserMonthlyTrendGraphResponse.MonthlyDataPoint.builder()
                                .month(entry.getKey())
                                .repeatPurchaserCount(entry.getValue())
                                .build())
                        .toList();

        return RepeatPurchaserMonthlyTrendGraphResponse.builder()
                .trend(trend)
                .build();
    }

    /**
     * 구매 주기 분포를 조회합니다. (구간별 히스토그램)
     */
    public PurchaseCycleDistributionGraphResponse getPurchaseCycleDistributionGraph() {
        List<Long> averageIntervals = computeAverageIntervalsForRepeatPurchasers();

        List<PurchaseCycleDistributionGraphResponse.IntervalBucket> distribution = List.of(
                intervalBucket("0-7일", 0, 7, averageIntervals.stream().filter(d -> d >= 0 && d <= 7).count()),
                intervalBucket("8-14일", 8, 14, averageIntervals.stream().filter(d -> d >= 8 && d <= 14).count()),
                intervalBucket("15-30일", 15, 30, averageIntervals.stream().filter(d -> d >= 15 && d <= 30).count()),
                intervalBucket("31-60일", 31, 60, averageIntervals.stream().filter(d -> d >= 31 && d <= 60).count()),
                intervalBucket("60일+", 61, null, averageIntervals.stream().filter(d -> d > 60).count())
        );

        return PurchaseCycleDistributionGraphResponse.builder()
                .distribution(distribution)
                .totalRepeatPurchasers((long) averageIntervals.size())
                .build();
    }

    /**
     * 구매 주기 요약 통계를 조회합니다.
     */
    public PurchaseCycleSummaryResponse getPurchaseCycleSummary() {
        List<Long> averageIntervals = computeAverageIntervalsForRepeatPurchasers();

        if (averageIntervals.isEmpty()) {
            return PurchaseCycleSummaryResponse.builder()
                    .averageDays(0.0)
                    .medianDays(0.0)
                    .minDays(0L)
                    .maxDays(0L)
                    .totalRepeatPurchasers(0L)
                    .build();
        }

        Collections.sort(averageIntervals);
        double average = averageIntervals.stream().mapToLong(Long::longValue).average().orElse(0.0);
        int size = averageIntervals.size();
        double median = size % 2 == 0
                ? (averageIntervals.get(size / 2 - 1) + averageIntervals.get(size / 2)) / 2.0
                : averageIntervals.get(size / 2);

        return PurchaseCycleSummaryResponse.builder()
                .averageDays(Math.round(average * 10.0) / 10.0)
                .medianDays(Math.round(median * 10.0) / 10.0)
                .minDays(averageIntervals.get(0))
                .maxDays(averageIntervals.get(size - 1))
                .totalRepeatPurchasers((long) size)
                .build();
    }

    /**
     * 전체 사용자의 예약 빈도 분포를 조회합니다. (1회, 2회, 3회, 4회, 5회+)
     */
    public LoyalCustomerFrequencyGraphResponse getLoyalCustomerFrequencyGraph() {
        List<Object[]> allCounts = reservationRepository.findAllUserReservationCounts();

        Map<Long, Long> frequencyMap = allCounts.stream()
                .collect(Collectors.groupingBy(
                        row -> Math.min((Long) row[1], 5L),
                        Collectors.counting()
                ));

        List<LoyalCustomerFrequencyGraphResponse.FrequencyBucket> distribution =
                LongStream.rangeClosed(1, 5)
                        .mapToObj(count -> LoyalCustomerFrequencyGraphResponse.FrequencyBucket.builder()
                                .purchaseCount(count == 5 ? "5+" : String.valueOf(count))
                                .userCount(frequencyMap.getOrDefault(count, 0L))
                                .build())
                        .toList();

        long totalUsers = distribution.stream()
                .mapToLong(LoyalCustomerFrequencyGraphResponse.FrequencyBucket::getUserCount)
                .sum();

        return LoyalCustomerFrequencyGraphResponse.builder()
                .distribution(distribution)
                .totalUsers(totalUsers)
                .build();
    }

    // ===== 기간별 성과 분석 추이 API =====

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM.dd");

    /**
     * 재구매자 비중 일별 추이를 조회합니다.
     * 각 날짜에 예약한 사용자 중 최초 구매자/재구매자 비율을 계산합니다.
     */
    public RepeatBuyerRatioTrendResponse getRepeatBuyerRatioTrend(LocalDate startDate, LocalDate endDate) {
        List<Reservation> allReservations = reservationRepository.findAllValidReservationsWithUser();

        // 사용자별 최초 구매일 맵
        Map<Long, LocalDate> firstPurchaseDateByUser = reservationRepository.findFirstReservationDateByUser().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((LocalDateTime) row[1]).toLocalDate()
                ));

        // 날짜별 예약한 고유 사용자 그룹
        Map<LocalDate, Set<Long>> usersByDate = allReservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().toLocalDate(),
                        Collectors.mapping(r -> r.getUser().getUserCode(), Collectors.toSet())
                ));

        List<RepeatBuyerRatioTrendResponse.Entry> data = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> {
                    Set<Long> usersOnDate = usersByDate.getOrDefault(date, Collections.emptySet());
                    if (usersOnDate.isEmpty()) {
                        return RepeatBuyerRatioTrendResponse.Entry.builder()
                                .date(date.format(DATE_FORMATTER))
                                .first(0).re(0).build();
                    }
                    long firstCount = usersOnDate.stream()
                            .filter(userCode -> date.equals(firstPurchaseDateByUser.get(userCode)))
                            .count();
                    int total = usersOnDate.size();
                    int firstPct = (int) Math.round(firstCount * 100.0 / total);
                    return RepeatBuyerRatioTrendResponse.Entry.builder()
                            .date(date.format(DATE_FORMATTER))
                            .first(firstPct).re(100 - firstPct).build();
                })
                .toList();

        return RepeatBuyerRatioTrendResponse.builder().data(data).build();
    }

    /**
     * 최초 구매자의 재구매 확률 추이를 조회합니다.
     * 각 날짜까지 누적된 최초 구매자 중 재구매(2회+)한 비율을 계산합니다.
     */
    public RepurchaseProbabilityTrendResponse getRepurchaseProbabilityTrend(LocalDate startDate, LocalDate endDate) {
        // 사용자별 최초 구매일
        Map<Long, LocalDate> firstPurchaseDateByUser = reservationRepository.findFirstReservationDateByUser().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((LocalDateTime) row[1]).toLocalDate()
                ));

        // 사용자별 유효 예약 횟수
        Map<Long, Long> reservationCountByUser = reservationRepository.findAllUserReservationCounts().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        // 재구매한 사용자 셋 (2회 이상)
        Set<Long> usersWhoRepurchased = reservationCountByUser.entrySet().stream()
                .filter(e -> e.getValue() >= 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // 최초 구매일 기준 정렬된 사용자 목록
        List<Map.Entry<Long, LocalDate>> sortedEntries = firstPurchaseDateByUser.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();

        List<RepurchaseProbabilityTrendResponse.DataPoint> data = new ArrayList<>();
        int cumulativeTotal = 0;
        int cumulativeRepurchased = 0;
        int entryIdx = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 이 날짜까지의 누적 최초 구매자 수 갱신
            while (entryIdx < sortedEntries.size() && !sortedEntries.get(entryIdx).getValue().isAfter(date)) {
                cumulativeTotal++;
                if (usersWhoRepurchased.contains(sortedEntries.get(entryIdx).getKey())) {
                    cumulativeRepurchased++;
                }
                entryIdx++;
            }

            int probability = cumulativeTotal > 0 ? (int) Math.round(cumulativeRepurchased * 100.0 / cumulativeTotal) : 0;
            data.add(RepurchaseProbabilityTrendResponse.DataPoint.builder()
                    .x(date.format(DATE_FORMATTER)).y(probability).build());
        }

        return RepurchaseProbabilityTrendResponse.builder().data(data).build();
    }

    /**
     * 재구매 주기 일별 추이를 조회합니다.
     * 각 날짜에 재구매를 한 사용자들의 평균 구매 간격(일)을 계산합니다.
     */
    public RepurchaseCycleTrendResponse getRepurchaseCycleTrend(LocalDate startDate, LocalDate endDate) {
        List<Reservation> allReservations = reservationRepository.findAllValidReservationsWithUser();

        // 사용자별 예약을 날짜순 정렬
        Map<Long, List<Reservation>> reservationsByUser = allReservations.stream()
                .collect(Collectors.groupingBy(r -> r.getUser().getUserCode()));

        // 날짜별 재구매 간격 수집 (두 번째 예약의 날짜에 태깅)
        Map<LocalDate, List<Long>> gapsByDate = new HashMap<>();

        for (List<Reservation> userReservations : reservationsByUser.values()) {
            List<Reservation> sorted = userReservations.stream()
                    .sorted(Comparator.comparing(Reservation::getCreatedAt))
                    .toList();

            for (int i = 1; i < sorted.size(); i++) {
                long gap = ChronoUnit.DAYS.between(
                        sorted.get(i - 1).getCreatedAt(),
                        sorted.get(i).getCreatedAt()
                );
                LocalDate gapDate = sorted.get(i).getCreatedAt().toLocalDate();
                gapsByDate.computeIfAbsent(gapDate, k -> new ArrayList<>()).add(gap);
            }
        }

        List<RepurchaseCycleTrendResponse.DataPoint> data = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> {
                    List<Long> gaps = gapsByDate.getOrDefault(date, Collections.emptyList());
                    int avg = gaps.isEmpty() ? 0 : (int) Math.round(gaps.stream().mapToLong(Long::longValue).average().orElse(0));
                    return RepurchaseCycleTrendResponse.DataPoint.builder()
                            .x(date.format(DATE_FORMATTER)).y(avg).build();
                })
                .toList();

        return RepurchaseCycleTrendResponse.builder().data(data).build();
    }

    /**
     * 충성고객 비중 일별 분포 추이를 조회합니다.
     * 각 날짜까지의 누적 구매 횟수 기준으로 사용자 레벨 분포를 계산합니다.
     */
    public LoyalCustomerDistributionTrendResponse getLoyalCustomerDistributionTrend(LocalDate startDate, LocalDate endDate) {
        List<Reservation> allReservations = reservationRepository.findAllValidReservationsWithUser();

        // 사용자별 예약 날짜 리스트 (날짜순)
        Map<Long, List<LocalDate>> reservationDatesByUser = allReservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getUser().getUserCode(),
                        Collectors.mapping(r -> r.getCreatedAt().toLocalDate(), Collectors.toList())
                ));

        // 날짜별 이벤트 수집: (date -> list of userCodes with new reservation)
        Map<LocalDate, List<Long>> eventsByDate = new TreeMap<>();
        for (Map.Entry<Long, List<LocalDate>> entry : reservationDatesByUser.entrySet()) {
            for (LocalDate date : entry.getValue()) {
                eventsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(entry.getKey());
            }
        }

        // 사용자별 현재 누적 예약 수
        Map<Long, Integer> userCumulativeCount = new HashMap<>();
        // 레벨별 사용자 수 (lv1~lv5)
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

        List<LoyalCustomerDistributionTrendResponse.Entry> data = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 해당 날짜의 이벤트 처리
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
                data.add(LoyalCustomerDistributionTrendResponse.Entry.builder()
                        .date(date.format(DATE_FORMATTER))
                        .lv1(0).lv2(0).lv3(0).lv4(0).lv5(0).build());
            } else {
                int lv1Pct = (int) Math.round(levelCounts[1] * 100.0 / totalUsers);
                int lv2Pct = (int) Math.round(levelCounts[2] * 100.0 / totalUsers);
                int lv3Pct = (int) Math.round(levelCounts[3] * 100.0 / totalUsers);
                int lv4Pct = (int) Math.round(levelCounts[4] * 100.0 / totalUsers);
                int lv5Pct = 100 - lv1Pct - lv2Pct - lv3Pct - lv4Pct;
                data.add(LoyalCustomerDistributionTrendResponse.Entry.builder()
                        .date(date.format(DATE_FORMATTER))
                        .lv1(lv1Pct).lv2(lv2Pct).lv3(lv3Pct).lv4(lv4Pct).lv5(lv5Pct).build());
            }
        }

        return LoyalCustomerDistributionTrendResponse.builder().data(data).build();
    }

    private int levelIndex(int count) {
        return Math.min(count, 5);
    }

    // ===== Private Helpers =====

    private List<Long> computeAverageIntervalsForRepeatPurchasers() {
        List<Object[]> stats = reservationRepository.findLoyalCustomerStats();
        List<Long> userCodes = stats.stream()
                .map(row -> (Long) row[0])
                .toList();

        if (userCodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Reservation> reservations = reservationRepository.findAllByUserCodesWithDetails(userCodes);
        Map<Long, List<Reservation>> reservationsByUser = reservations.stream()
                .collect(Collectors.groupingBy(r -> r.getUser().getUserCode()));

        List<Long> averageIntervals = new ArrayList<>();

        for (Map.Entry<Long, List<Reservation>> entry : reservationsByUser.entrySet()) {
            List<Reservation> userReservations = entry.getValue().stream()
                    .sorted(Comparator.comparing(Reservation::getCreatedAt))
                    .toList();

            if (userReservations.size() < 2) continue;

            long totalDays = 0;
            int intervalCount = 0;
            for (int i = 1; i < userReservations.size(); i++) {
                totalDays += ChronoUnit.DAYS.between(
                        userReservations.get(i - 1).getCreatedAt(),
                        userReservations.get(i).getCreatedAt()
                );
                intervalCount++;
            }

            averageIntervals.add(intervalCount > 0 ? totalDays / intervalCount : 0);
        }

        return averageIntervals;
    }

    private PurchaseCycleDistributionGraphResponse.IntervalBucket intervalBucket(
            String label, Integer min, Integer max, Long count) {
        return PurchaseCycleDistributionGraphResponse.IntervalBucket.builder()
                .intervalLabel(label)
                .minDays(min)
                .maxDays(max)
                .userCount(count)
                .build();
    }
}
