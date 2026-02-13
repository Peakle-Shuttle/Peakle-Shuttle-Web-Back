package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "충성 고객 응답")
public class LoyalCustomerResponse {

    @Schema(description = "사용자 코드", example = "1")
    private final Long userCode;

    @Schema(description = "사용자 이름", example = "홍길동")
    private final String userName;

    @Schema(description = "예약 횟수", example = "5")
    private final Long reservationCount;

    public static LoyalCustomerResponse of(Long userCode, String userName, Long reservationCount) {
        return LoyalCustomerResponse.builder()
                .userCode(userCode)
                .userName(userName)
                .reservationCount(reservationCount)
                .build();
    }
}
