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
}
