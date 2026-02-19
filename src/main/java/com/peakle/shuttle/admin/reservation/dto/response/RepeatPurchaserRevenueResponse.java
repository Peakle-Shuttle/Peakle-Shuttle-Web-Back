package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "재구매자 매출 응답 (Mock)")
public class RepeatPurchaserRevenueResponse {

    @Schema(description = "총 매출", example = "1500000")
    private final Long totalRevenue;

    @Schema(description = "재구매자 매출", example = "1200000")
    private final Long repeatPurchaserRevenue;

    @Schema(description = "재구매자 매출 비율 (%)", example = "80.0")
    private final Double ratio;

    public static RepeatPurchaserRevenueResponse mock() {
        return RepeatPurchaserRevenueResponse.builder()
                .totalRevenue(1500000L)
                .repeatPurchaserRevenue(1200000L)
                .ratio(80.0)
                .build();
    }
}
