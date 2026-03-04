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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reservation")
@RequiredArgsConstructor
@Tag(name = "Admin Reservation", description = "관리자 예약 관리 API")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

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
     * 예약을 삭제합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param reservationId 삭제할 예약 ID
     */
    @Operation(summary = "예약 삭제", description = "예약을 삭제합니다.")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long reservationId
    ) {
        adminReservationService.deleteReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 충성 고객 목록을 조회합니다. (2회 이상 예약한 사용자)
     *
     * @param user 인증된 관리자 사용자 정보
     * @return 충성 고객 목록
     */
    @Operation(summary = "충성 고객 비중 조회", description = "2회 이상 예약한 사용자 목록과 예약 횟수를 조회합니다.")
    @GetMapping("/stats/loyal-customers")
    public ResponseEntity<List<LoyalCustomerResponse>> getLoyalCustomers(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getLoyalCustomers());
    }

    /**
     * 재구매자 상세 목록을 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @return 재구매자 목록
     */
    @Operation(summary = "재구매자 조회", description = "2회 이상 예약한 사용자의 상세 정보와 예약 내역을 조회합니다.")
    @GetMapping("/stats/repeat-purchasers")
    public ResponseEntity<List<RepeatPurchaserResponse>> getRepeatPurchasers(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getRepeatPurchasers());
    }

    /**
     * 재구매자 매출을 조회합니다. (Mock 데이터)
     *
     * @param user 인증된 관리자 사용자 정보
     * @return 재구매자 매출 정보
     */
    @Operation(summary = "재구매자 매출 조회", description = "재구매자의 매출 정보를 조회합니다. (Mock 데이터)")
    @GetMapping("/stats/repeat-revenue")
    public ResponseEntity<RepeatPurchaserRevenueResponse> getRepeatPurchaserRevenue(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getRepeatPurchaserRevenue());
    }

    /**
     * 재구매자 비율을 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @return 재구매자 비율 정보
     */
    @Operation(summary = "재구매자 비율 조회", description = "전체 사용자 중 2회 이상 예약한 사용자의 비율을 조회합니다.")
    @GetMapping("/stats/repeat-ratio")
    public ResponseEntity<RepeatPurchaserRatioResponse> getRepeatPurchaserRatio(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getRepeatPurchaserRatio());
    }

    /**
     * 재구매자의 구매 주기를 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @return 구매 주기 정보 목록
     */
    @Operation(summary = "구매 주기 조회", description = "재구매자의 가장 최근 예약 정보와 경과일을 조회합니다.")
    @GetMapping("/stats/purchase-cycle")
    public ResponseEntity<List<PurchaseCycleResponse>> getPurchaseCycle(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getPurchaseCycle());
    }

    // ===== 그래프용 집계 API =====

    @Operation(summary = "그래프용 재구매자 빈도 분포 그래프", description = "재구매 횟수별 사용자 수 분포를 조회합니다. (바 차트용)")
    @GetMapping("/stats/repeat-purchasers/graph/frequency")
    public ResponseEntity<RepeatPurchaserFrequencyGraphResponse> getRepeatPurchaserFrequencyGraph(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getRepeatPurchaserFrequencyGraph());
    }

    @Operation(summary = "그래프용 재구매자 월별 추이 그래프", description = "월별 재구매자 수 추이를 조회합니다. (라인 차트용)")
    @GetMapping("/stats/repeat-purchasers/graph/monthly-trend")
    public ResponseEntity<RepeatPurchaserMonthlyTrendGraphResponse> getRepeatPurchaserMonthlyTrendGraph(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getRepeatPurchaserMonthlyTrendGraph());
    }

    @Operation(summary = "그래프용 구매 주기 분포 그래프", description = "구매 주기 구간별 사용자 수 분포를 조회합니다. (히스토그램용)")
    @GetMapping("/stats/purchase-cycle/graph/distribution")
    public ResponseEntity<PurchaseCycleDistributionGraphResponse> getPurchaseCycleDistributionGraph(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getPurchaseCycleDistributionGraph());
    }

    @Operation(summary = "그래프용 구매 주기 요약 통계", description = "구매 주기 평균, 중앙값, 최소, 최대를 조회합니다.")
    @GetMapping("/stats/purchase-cycle/graph/summary")
    public ResponseEntity<PurchaseCycleSummaryResponse> getPurchaseCycleSummary(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getPurchaseCycleSummary());
    }

    @Operation(summary = "그래프용 충성 고객 빈도 분포 그래프", description = "예약 횟수별 사용자 수 분포를 조회합니다. (바 차트용)")
    @GetMapping("/stats/loyal-customers/graph/frequency")
    public ResponseEntity<LoyalCustomerFrequencyGraphResponse> getLoyalCustomerFrequencyGraph(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminReservationService.getLoyalCustomerFrequencyGraph());
    }

    // ===== 기간별 성과 분석 추이 API =====

    @Operation(summary = "그래프용 기간별 재구매자 비중 추이 조회", description = "일별 최초 구매자 vs 재구매자 비중 추이를 조회합니다.")
    @GetMapping("/stats/repeat-buyer-ratio-trend")
    public ResponseEntity<RepeatBuyerRatioTrendResponse> getRepeatBuyerRatioTrend(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminReservationService.getRepeatBuyerRatioTrend(startDate, endDate));
    }

    @Operation(summary = "그래프용 기간별 최초 구매자 재구매 확률 추이 조회", description = "최초 구매자의 누적 재구매 확률 추이를 조회합니다.")
    @GetMapping("/stats/repurchase-probability-trend")
    public ResponseEntity<RepurchaseProbabilityTrendResponse> getRepurchaseProbabilityTrend(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminReservationService.getRepurchaseProbabilityTrend(startDate, endDate));
    }

    @Operation(summary = "그래프용 기간별 재구매 주기 추이 조회", description = "일별 평균 재구매 주기(일) 추이를 조회합니다.")
    @GetMapping("/stats/repurchase-cycle-trend")
    public ResponseEntity<RepurchaseCycleTrendResponse> getRepurchaseCycleTrend(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminReservationService.getRepurchaseCycleTrend(startDate, endDate));
    }

    @Operation(summary = "그래프용 기간별 충성고객 비중 추이 조회", description = "일별 누적 구매 횟수 기준 사용자 레벨 분포 추이를 조회합니다.")
    @GetMapping("/stats/loyal-customer-distribution-trend")
    public ResponseEntity<LoyalCustomerDistributionTrendResponse> getLoyalCustomerDistributionTrend(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminReservationService.getLoyalCustomerDistributionTrend(startDate, endDate));
    }
}
