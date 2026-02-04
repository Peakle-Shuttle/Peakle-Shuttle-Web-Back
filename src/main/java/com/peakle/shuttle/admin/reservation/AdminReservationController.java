package com.peakle.shuttle.admin.reservation;

import com.peakle.shuttle.admin.reservation.dto.AdminReservationResponse;
import com.peakle.shuttle.admin.reservation.dto.ReservationUpdateRequest;
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
@RequestMapping("/api/admin/dispatch/reservation")
@RequiredArgsConstructor
@Tag(name = "Admin Reservation", description = "관리자 예약 관리 API")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    @Operation(summary = "예약 목록 일괄 조회", description = "전체 예약 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AdminReservationResponse>> getReservations(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getAllReservations());
    }

    @Operation(summary = "특정 사용자 예약 조회", description = "특정 사용자의 예약 목록을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AdminReservationResponse>> getReservationsByUser(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(adminReservationService.getReservationsByUser(userId));
    }

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

    @Operation(summary = "예약 삭제", description = "예약을 삭제합니다.")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long reservationId
    ) {
        adminReservationService.deleteReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}
