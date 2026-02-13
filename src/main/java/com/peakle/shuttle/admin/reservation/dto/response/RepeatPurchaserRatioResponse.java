package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "재구매자 비율 응답")
public class RepeatPurchaserRatioResponse {

    @Schema(description = "전체 사용자 수", example = "100")
    private final Long totalUsers;

    @Schema(description = "재구매자 수", example = "35")
    private final Long repeatPurchasers;

    @Schema(description = "재구매자 비율 (%)", example = "35.0")
    private final Double ratio;

    public static RepeatPurchaserRatioResponse of(Long totalUsers, Long repeatPurchasers) {
        double ratio = totalUsers > 0 ? (repeatPurchasers * 100.0) / totalUsers : 0.0;
        return RepeatPurchaserRatioResponse.builder()
                .totalUsers(totalUsers)
                .repeatPurchasers(repeatPurchasers)
                .ratio(Math.round(ratio * 10.0) / 10.0)
                .build();
    }
}
