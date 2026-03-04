package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "구매 주기 분포 그래프 응답")
public class PurchaseCycleDistributionGraphResponse {

    @Schema(description = "구간별 분포 데이터")
    private final List<IntervalBucket> distribution;

    @Schema(description = "총 분석 대상 사용자 수", example = "35")
    private final Long totalRepeatPurchasers;

    @Getter
    @Builder
    @Schema(description = "구간별 분포 버킷")
    public static class IntervalBucket {

        @Schema(description = "구간 라벨", example = "0-7일")
        private final String intervalLabel;

        @Schema(description = "구간 최소일", example = "0")
        private final Integer minDays;

        @Schema(description = "구간 최대일 (null이면 상한 없음)", example = "7")
        private final Integer maxDays;

        @Schema(description = "해당 구간의 사용자 수", example = "8")
        private final Long userCount;
    }
}
