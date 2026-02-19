package com.peakle.shuttle.reservation;

import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import com.peakle.shuttle.reservation.dto.request.ReservationCreateRequest;
import com.peakle.shuttle.reservation.dto.request.ReservationUpdateRequest;
import com.peakle.shuttle.reservation.dto.response.ReservationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "예약 관리 API")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 셔틀 예약을 생성합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 예약 생성 요청 정보
     * @return 생성된 예약 정보
     */
    @Operation(summary = "예약 생성", description = "셔틀 예약을 생성합니다.")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        return ResponseEntity.ok(reservationService.createReservation(user.code(), request));
    }

    /**
     * 특정 예약 정보를 조회합니다.
     *
     * @param user 인증된 사용자 정보
     * @param reservationId 예약 ID
     * @return 예약 상세 정보
     */
    @Operation(summary = "예약 조회", description = "특정 예약 정보를 조회합니다.")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservation(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(reservationService.getReservation(user.code(), reservationId));
    }

    /**
     * 내 예약 목록을 조회합니다.
     *
     * @param user 인증된 사용자 정보
     * @return 예약 목록
     */
    @Operation(summary = "내 예약 목록 조회", description = "내 예약 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(reservationService.getMyReservations(user.code()));
    }

    /**
     * 예약 정보를 변경합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 예약 변경 요청 정보
     */
    @Operation(summary = "예약 변경", description = "예약 정보를 변경합니다.")
    @PatchMapping
    public ResponseEntity<Void> updateReservation(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody ReservationUpdateRequest request
    ) {
        reservationService.updateReservation(user.code(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 예약을 취소합니다.
     *
     * @param user 인증된 사용자 정보
     * @param reservationCode 예약 코드
     */
    @Operation(summary = "예약 취소", description = "예약을 취소합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteReservation(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam Long reservationCode
    ) {
        reservationService.deleteReservation(user.code(), reservationCode);
        return ResponseEntity.noContent().build();
    }
}
