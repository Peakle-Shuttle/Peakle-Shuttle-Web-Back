package com.peakle.shuttle.reservation.repository;

import com.peakle.shuttle.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationCode(Long reservationCode);
}
