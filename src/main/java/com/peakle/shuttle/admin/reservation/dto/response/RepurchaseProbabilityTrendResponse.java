package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "최초 구매자 재구매 확률 추이 그래프 응답")
public class RepurchaseProbabilityTrendResponse {

    @Schema(description = "일별 재구매 확률 데이터")
    private final List<DataPoint> data;

    @Getter
    @Builder
    @Schema(description = "데이터 포인트")
    public static class DataPoint {

        @Schema(description = "날짜", example = "01.10")
        private final String x;

        @Schema(description = "재구매 확률 (%)", example = "28")
        private final Integer y;
    }
}
