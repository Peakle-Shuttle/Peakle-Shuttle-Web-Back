package com.peakle.shuttle.admin.reservation;

import com.peakle.shuttle.admin.reservation.dto.AdminReservationResponse;
import com.peakle.shuttle.admin.reservation.dto.ReservationUpdateRequest;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.reservation.entity.Reservation;
import com.peakle.shuttle.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReservationService {

    private final ReservationRepository reservationRepository;

    public List<AdminReservationResponse> getAllReservations() {
        return reservationRepository.findAllWithDetails().stream()
                .map(AdminReservationResponse::from)
                .toList();
    }

    public List<AdminReservationResponse> getReservationsByUser(Long userCode) {
        return reservationRepository.findAllByUserCodeWithDetails(userCode).stream()
                .map(AdminReservationResponse::from)
                .toList();
    }

    @Transactional
    public void updateReservation(Long reservationCode, ReservationUpdateRequest request) {
        Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_RESERVATION));
        reservation.updateReservationCount(request.reservationCount());
    }

    @Transactional
    public void deleteReservation(Long reservationCode) {
        Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_RESERVATION));
        reservationRepository.delete(reservation);
    }
}
