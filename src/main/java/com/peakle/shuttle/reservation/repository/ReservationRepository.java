package com.peakle.shuttle.reservation.repository;

import com.peakle.shuttle.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationCode(Long reservationCode);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.dispatch d JOIN FETCH d.course")
    List<Reservation> findAllWithDetails();

    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.dispatch d JOIN FETCH d.course WHERE r.user.userCode = :userCode")
    List<Reservation> findAllByUserCodeWithDetails(Long userCode);

    Optional<Reservation> findByReservationCodeAndUserUserCode(Long reservationCode, Long userCode);

    // 사용자별 예약 횟수 집계 (2회 이상 예약한 사용자)
    @Query("SELECT r.user.userCode, COUNT(r) FROM Reservation r GROUP BY r.user.userCode HAVING COUNT(r) >= 2")
    List<Object[]> findLoyalCustomerStats();

    // 전체 예약한 사용자 수 (중복 제거)
    @Query("SELECT COUNT(DISTINCT r.user.userCode) FROM Reservation r")
    Long countDistinctUsers();

    // 특정 사용자들의 예약 정보 조회 (User 포함)
    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.dispatch d JOIN FETCH d.course WHERE r.user.userCode IN :userCodes ORDER BY r.user.userCode, r.createdAt DESC")
    List<Reservation> findAllByUserCodesWithDetails(@Param("userCodes") List<Long> userCodes);
}
