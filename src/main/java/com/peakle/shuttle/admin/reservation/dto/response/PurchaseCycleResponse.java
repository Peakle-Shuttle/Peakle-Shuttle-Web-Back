package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "구매 주기 응답")
public class PurchaseCycleResponse {

    @Schema(description = "사용자 코드", example = "1")
    private final Long userCode;

    @Schema(description = "사용자 이름", example = "홍길동")
    private final String userName;

    @Schema(description = "마지막 예약 일시", example = "2025-02-10T14:30:00")
    private final LocalDateTime lastReservationDate;

    @Schema(description = "마지막 예약 후 경과일", example = "2")
    private final Long daysSinceLastReservation;

    @Schema(description = "총 예약 횟수", example = "5")
    private final Long totalReservationCount;
}
