package com.peakle.shuttle.admin.reservation;

import com.peakle.shuttle.admin.reservation.dto.request.ReservationUpdateRequest;
import com.peakle.shuttle.admin.reservation.dto.response.*;
import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservation")
@RequiredArgsConstructor
@Tag(name = "Admin Reservation", description = "관리자 예약 관리 API")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    /**
     * 전체 노선 목록을 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @return 노선 목록 (코드, 이름)
     */
    @Operation(summary = "노선 목록 조회", description = "전체 노선 목록을 조회합니다.")
    @GetMapping("/course")
    public ResponseEntity<List<AdminCourseSimpleResponse>> getCourses(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getCourses());
    }

    /**
     * 특정 노선의 배차 목록을 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param courseId 노선 ID
     * @return 배차 목록 (코드, 시간)
     */
    @Operation(summary = "배차 목록 조회", description = "특정 노선의 배차 목록을 조회합니다.")
    @GetMapping("/dispatch/{courseId}")
    public ResponseEntity<List<AdminDispatchSimpleResponse>> getDispatches(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(adminReservationService.getDispatchesByCourse(courseId));
    }

    /**
     * 전체 예약 목록을 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @return 예약 목록
     */
    @Operation(summary = "예약 목록 일괄 조회", description = "전체 예약 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AdminReservationResponse>> getReservations(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getAllReservations());
    }

    /**
     * 특정 사용자의 예약 목록을 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param userId 조회할 사용자 ID
     * @return 예약 목록
     */
    @Operation(summary = "특정 사용자 예약 조회", description = "특정 사용자의 예약 목록을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AdminReservationResponse>> getReservationsByUser(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(adminReservationService.getReservationsByUser(userId));
    }

    /**
     * 특정 예약의 상세 정보를 조회합니다. (예약 정보 + 예약자 정보)
     *
     * @param user 인증된 관리자 사용자 정보
     * @param reservationId 조회할 예약 ID
     * @return 예약 상세 정보
     */
    @Operation(summary = "예약 상세 조회", description = "특정 예약의 상세 정보와 예약자 정보를 조회합니다.")
    @GetMapping("/{reservationId}")
    public ResponseEntity<AdminReservationDetailResponse> getReservationDetail(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(adminReservationService.getReservationDetail(reservationId));
    }

    /**
     * 예약 정보를 수정합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param reservationId 수정할 예약 ID
     * @param request 예약 수정 요청 정보
     */
    @Operation(summary = "예약 수정", description = "예약 정보를 수정합니다.")
    @PatchMapping("/{reservationId}")
    public ResponseEntity<Void> updateReservation(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long reservationId,
            @Valid @RequestBody ReservationUpdateRequest request
    ) {
        adminReservationService.updateReservation(reservationId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 예약을 취소합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param reservationId 취소할 예약 ID
     */
    @Operation(summary = "예약 취소", description = "예약을 취소합니다.")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long reservationId
    ) {
        adminReservationService.deleteReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}
