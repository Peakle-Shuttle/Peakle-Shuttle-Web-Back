package com.peakle.shuttle.admin.reservation;

import com.peakle.shuttle.admin.reservation.dto.request.ReservationUpdateRequest;
import com.peakle.shuttle.admin.reservation.dto.response.*;
import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.reservation.entity.Reservation;
import com.peakle.shuttle.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReservationService {

    private final ReservationRepository reservationRepository;
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
        reservation.updateReservationCount(request.reservationCount());
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
                                    .dispatchDay(r.getDispatch().getDispatchDay())
                                    .dispatchStartTime(r.getDispatch().getDispatchStartTime())
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
}
