package com.peakle.shuttle.admin.reservation;

import com.peakle.shuttle.admin.reservation.dto.request.ReservationUpdateRequest;
import com.peakle.shuttle.admin.reservation.dto.response.*;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.course.entity.Dispatch;
import com.peakle.shuttle.course.repository.CourseRepository;
import com.peakle.shuttle.course.repository.DispatchRepository;
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
    private final CourseRepository courseRepository;
    private final DispatchRepository dispatchRepository;

    /**
     * 전체 노선 목록을 조회합니다.
     *
     * @return 노선 목록 (코드, 이름)
     */
    public List<AdminCourseSimpleResponse> getCourses() {
        return courseRepository.findAll().stream()
                .map(AdminCourseSimpleResponse::from)
                .toList();
    }

    /**
     * 특정 노선의 배차 목록을 조회합니다.
     *
     * @param courseCode 노선 코드
     * @return 배차 목록 (코드, 시간)
     */
    public List<AdminDispatchSimpleResponse> getDispatchesByCourse(Long courseCode) {
        return dispatchRepository.findAllByCourseCourseCode(courseCode).stream()
                .map(AdminDispatchSimpleResponse::from)
                .toList();
    }

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
        reservation.cancel();
    }
}
