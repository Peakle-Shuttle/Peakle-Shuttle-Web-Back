package com.peakle.shuttle.reservation.repository;

import com.peakle.shuttle.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationCode(Long reservationCode);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.dispatch d JOIN FETCH d.course")
    List<Reservation> findAllWithDetails();

    @Query("SELECT r FROM Reservation r JOIN FETCH r.user JOIN FETCH r.dispatch d JOIN FETCH d.course WHERE r.user.userCode = :userCode")
    List<Reservation> findAllByUserCodeWithDetails(Long userCode);

    Optional<Reservation> findByReservationCodeAndUserUserCode(Long reservationCode, Long userCode);
}
