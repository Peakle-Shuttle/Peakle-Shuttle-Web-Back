package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "재구매 주기 일별 추이 그래프 응답")
public class RepurchaseCycleTrendResponse {

    @Schema(description = "일별 평균 재구매 주기 데이터")
    private final List<DataPoint> data;

    @Getter
    @Builder
    @Schema(description = "데이터 포인트")
    public static class DataPoint {

        @Schema(description = "날짜", example = "01.10")
        private final String x;

        @Schema(description = "평균 재구매 주기 (일)", example = "12")
        private final Integer y;
    }
}
