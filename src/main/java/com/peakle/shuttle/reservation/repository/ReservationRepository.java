package com.peakle.shuttle.reservation.repository;

import com.peakle.shuttle.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationCode(Long reservationCode);

    @Query("SELECT DISTINCT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.dispatch d JOIN FETCH d.course c")
    List<Reservation> findAllWithDetails();

    @Query("SELECT DISTINCT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.dispatch d JOIN FETCH d.course c WHERE r.user.userCode = :userCode")
    List<Reservation> findAllByUserCodeWithDetails(Long userCode);

    Optional<Reservation> findByReservationCodeAndUserUserCode(Long reservationCode, Long userCode);

    // 사용자별 예약 횟수 집계 (2회 이상 예약한 사용자)
    @Query("SELECT r.user.userCode, COUNT(r) FROM Reservation r GROUP BY r.user.userCode HAVING COUNT(r) >= 2")
    List<Object[]> findLoyalCustomerStats();

    // 전체 예약한 사용자 수 (중복 제거)
    @Query("SELECT COUNT(DISTINCT r.user.userCode) FROM Reservation r")
    Long countDistinctUsers();

    // 특정 사용자들의 예약 정보 조회 (User 포함)
    @Query("SELECT DISTINCT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.dispatch d JOIN FETCH d.course c WHERE r.user.userCode IN :userCodes ORDER BY r.user.userCode, r.createdAt DESC")
    List<Reservation> findAllByUserCodesWithDetails(@Param("userCodes") List<Long> userCodes);

    // 특정 예약의 전체 상세 정보 조회
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.user u " +
           "LEFT JOIN FETCH u.school " +
           "JOIN FETCH r.dispatch d " +
           "JOIN FETCH d.course c " +
           "WHERE r.reservationCode = :reservationCode")
    Optional<Reservation> findByReservationCodeWithFullDetails(@Param("reservationCode") Long reservationCode);

    // 사용자별 예약 횟수 집계 (전체 사용자)
    @Query("SELECT r.user.userCode, COUNT(r) FROM Reservation r GROUP BY r.user.userCode")
    List<Object[]> findAllUserReservationCounts();

    // 배차 시간이 지난 RESERVED 상태 예약 조회
    @Query("SELECT r FROM Reservation r JOIN FETCH r.dispatch d WHERE r.reservationStatus = 'RESERVED' AND d.dispatchDatetime < :now")
    List<Reservation> findExpiredReservations(@Param("now") LocalDateTime now);

    // 전체 유효 예약(RESERVED, REVIEWED)을 user와 함께 조회 (날짜순 정렬)
    @Query("SELECT r FROM Reservation r JOIN FETCH r.user WHERE r.reservationStatus IN ('RESERVED', 'REVIEWED') ORDER BY r.createdAt ASC")
    List<Reservation> findAllValidReservationsWithUser();

    // 사용자별 총 구매금액 집계
    @Query("SELECT r.user.userCode, SUM(r.reservationCount * d.course.courseCost) FROM Reservation r JOIN r.dispatch d GROUP BY r.user.userCode")
    List<Object[]> findAllUserTotalPurchaseAmounts();

    // 사용자별 최초 유효 예약일 조회
    @Query("SELECT r.user.userCode, MIN(r.createdAt) FROM Reservation r WHERE r.reservationStatus IN ('RESERVED', 'REVIEWED') GROUP BY r.user.userCode")
    List<Object[]> findFirstReservationDateByUser();

    // 일별 탑승자 수 집계 (배차 날짜 기준, 유효 예약만)
    @Query("SELECT CAST(d.dispatchDatetime AS LocalDate), SUM(r.reservationCount) " +
           "FROM Reservation r JOIN r.dispatch d " +
           "WHERE r.reservationStatus IN ('RESERVED', 'REVIEWED') " +
           "AND CAST(d.dispatchDatetime AS LocalDate) BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(d.dispatchDatetime AS LocalDate) " +
           "ORDER BY CAST(d.dispatchDatetime AS LocalDate)")
    List<Object[]> findDailyPassengerCounts(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 일별 주문 수 집계 (예약 생성일 기준, 유효 예약만)
    @Query("SELECT CAST(r.createdAt AS LocalDate), COUNT(r) " +
           "FROM Reservation r " +
           "WHERE r.reservationStatus IN ('RESERVED', 'REVIEWED') " +
           "AND CAST(r.createdAt AS LocalDate) BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(r.createdAt AS LocalDate) " +
           "ORDER BY CAST(r.createdAt AS LocalDate)")
    List<Object[]> findDailyOrderCounts(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 일별 매출 집계 (예약 생성일 기준, 유효 예약만)
    @Query("SELECT CAST(r.createdAt AS LocalDate), SUM(r.reservationCount * d.course.courseCost) " +
           "FROM Reservation r JOIN r.dispatch d " +
           "WHERE r.reservationStatus IN ('RESERVED', 'REVIEWED') " +
           "AND CAST(r.createdAt AS LocalDate) BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(r.createdAt AS LocalDate) " +
           "ORDER BY CAST(r.createdAt AS LocalDate)")
    List<Object[]> findDailyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
