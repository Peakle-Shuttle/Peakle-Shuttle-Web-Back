package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "충성 고객 빈도 분포 그래프 응답")
public class LoyalCustomerFrequencyGraphResponse {

    @Schema(description = "빈도 분포 데이터")
    private final List<FrequencyBucket> distribution;

    @Schema(description = "전체 사용자 수 (예약 1건 이상)", example = "100")
    private final Long totalUsers;

    @Getter
    @Builder
    @Schema(description = "빈도 분포 버킷")
    public static class FrequencyBucket {

        @Schema(description = "예약 횟수", example = "1")
        private final String purchaseCount;

        @Schema(description = "해당 횟수의 사용자 수", example = "45")
        private final Long userCount;
    }
}
