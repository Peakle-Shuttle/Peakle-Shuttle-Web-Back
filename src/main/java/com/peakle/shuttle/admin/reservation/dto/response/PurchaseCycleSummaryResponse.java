package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "구매 주기 요약 통계 응답")
public class PurchaseCycleSummaryResponse {

    @Schema(description = "평균 재구매 간격 (일)", example = "21.5")
    private final Double averageDays;

    @Schema(description = "중앙값 재구매 간격 (일)", example = "18.0")
    private final Double medianDays;

    @Schema(description = "최소 재구매 간격 (일)", example = "3")
    private final Long minDays;

    @Schema(description = "최대 재구매 간격 (일)", example = "90")
    private final Long maxDays;

    @Schema(description = "총 재구매자 수", example = "35")
    private final Long totalRepeatPurchasers;
}
