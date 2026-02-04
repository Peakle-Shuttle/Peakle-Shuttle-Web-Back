package com.peakle.shuttle.reservation;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.course.entity.Dispatch;
import com.peakle.shuttle.course.repository.DispatchRepository;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.reservation.dto.ReservationCreateRequest;
import com.peakle.shuttle.reservation.dto.ReservationResponse;
import com.peakle.shuttle.reservation.dto.ReservationUpdateRequest;
import com.peakle.shuttle.reservation.entity.Reservation;
import com.peakle.shuttle.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final DispatchRepository dispatchRepository;
    private final UserRepository userRepository;

    /**
     * 셔틀 예약을 생성합니다. 잔여 좌석을 확인하고 예약을 저장합니다.
     *
     * @param userCode 사용자 코드
     * @param request 예약 생성 요청 정보
     * @return 생성된 예약 정보
     * @throws AuthException 사용자/배차를 찾을 수 없거나 좌석이 부족한 경우
     */
    @Transactional
    public ReservationResponse createReservation(Long userCode, ReservationCreateRequest request) {
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));
        Dispatch dispatch = dispatchRepository.findByDispatchCode(request.dispatchCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_DISPATCH));

        Integer totalSeats = dispatch.getCourse().getCourseSeats();
        Integer occupied = dispatch.getDispatchOccupied() != null ? dispatch.getDispatchOccupied() : 0;
        if (totalSeats - occupied < request.count()) {
            throw new AuthException(ExceptionCode.NOT_ENOUGH_SEATS);
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .dispatch(dispatch)
                .reservationCount(request.count())
                .build();

        reservationRepository.save(reservation);
        dispatch.incrementOccupied(request.count());

        return ReservationResponse.from(reservation);
    }

    /**
     * 특정 예약 정보를 조회합니다.
     *
     * @param userCode 사용자 코드
     * @param reservationCode 예약 코드
     * @return 예약 상세 정보
     * @throws AuthException 예약을 찾을 수 없는 경우
     */
    public ReservationResponse getReservation(Long userCode, Long reservationCode) {
        Reservation reservation = reservationRepository.findByReservationCodeAndUserUserCode(reservationCode, userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_RESERVATION));
        return ReservationResponse.from(reservation);
    }

    /**
     * 내 예약 목록을 조회합니다.
     *
     * @param userCode 사용자 코드
     * @return 예약 목록
     */
    public List<ReservationResponse> getMyReservations(Long userCode) {
        return reservationRepository.findAllByUserCodeWithDetails(userCode).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    /**
     * 예약 정보를 변경합니다. 좌석 수 변경 시 잔여 좌석을 재검증합니다.
     *
     * @param userCode 사용자 코드
     * @param request 예약 변경 요청 정보
     * @throws AuthException 예약을 찾을 수 없거나 좌석이 부족한 경우
     */
    @Transactional
    public void updateReservation(Long userCode, ReservationUpdateRequest request) {
        Reservation reservation = reservationRepository.findByReservationCodeAndUserUserCode(request.reservationCode(), userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_RESERVATION));

        Dispatch dispatch = reservation.getDispatch();
        Integer totalSeats = dispatch.getCourse().getCourseSeats();
        Integer currentOccupied = dispatch.getDispatchOccupied() != null ? dispatch.getDispatchOccupied() : 0;
        Integer oldCount = reservation.getReservationCount();
        Integer diff = request.count() - oldCount;

        if (diff > 0 && (totalSeats - currentOccupied) < diff) {
            throw new AuthException(ExceptionCode.NOT_ENOUGH_SEATS);
        }

        reservation.updateReservationCount(request.count());
        if (diff > 0) {
            dispatch.incrementOccupied(diff);
        } else if (diff < 0) {
            dispatch.decrementOccupied(-diff);
        }
    }

    /**
     * 예약을 취소하고 점유 좌석을 반환합니다.
     *
     * @param userCode 사용자 코드
     * @param reservationCode 예약 코드
     * @throws AuthException 예약을 찾을 수 없는 경우
     */
    @Transactional
    public void deleteReservation(Long userCode, Long reservationCode) {
        Reservation reservation = reservationRepository.findByReservationCodeAndUserUserCode(reservationCode, userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_RESERVATION));

        Dispatch dispatch = reservation.getDispatch();
        dispatch.decrementOccupied(reservation.getReservationCount());
        reservationRepository.delete(reservation);
    }
}
