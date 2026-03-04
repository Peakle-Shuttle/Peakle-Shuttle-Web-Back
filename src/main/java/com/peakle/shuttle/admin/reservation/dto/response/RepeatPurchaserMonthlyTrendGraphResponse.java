package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "재구매자 월별 추이 그래프 응답")
public class RepeatPurchaserMonthlyTrendGraphResponse {

    @Schema(description = "월별 추이 데이터")
    private final List<MonthlyDataPoint> trend;

    @Getter
    @Builder
    @Schema(description = "월별 데이터 포인트")
    public static class MonthlyDataPoint {

        @Schema(description = "연월", example = "2025-01")
        private final String month;

        @Schema(description = "해당 월의 재구매자 수", example = "12")
        private final Long repeatPurchaserCount;
    }
}
