package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "재구매자 빈도 분포 그래프 응답")
public class RepeatPurchaserFrequencyGraphResponse {

    @Schema(description = "빈도 분포 데이터")
    private final List<FrequencyBucket> distribution;

    @Schema(description = "총 재구매자 수", example = "35")
    private final Long totalRepeatPurchasers;

    @Getter
    @Builder
    @Schema(description = "빈도 분포 버킷")
    public static class FrequencyBucket {

        @Schema(description = "예약 횟수", example = "2")
        private final String purchaseCount;

        @Schema(description = "해당 횟수의 사용자 수", example = "15")
        private final Long userCount;
    }
}
