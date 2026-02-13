package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "재구매자 응답")
public class RepeatPurchaserResponse {

    @Schema(description = "사용자 코드", example = "1")
    private final Long userCode;

    @Schema(description = "사용자 이름", example = "홍길동")
    private final String userName;

    @Schema(description = "이메일", example = "hong@example.com")
    private final String email;

    @Schema(description = "예약 횟수", example = "5")
    private final Long reservationCount;

    @Schema(description = "예약 목록")
    private final List<ReservationSummary> reservations;

    @Getter
    @Builder
    @Schema(description = "예약 요약 정보")
    public static class ReservationSummary {

        @Schema(description = "예약 코드", example = "101")
        private final Long reservationCode;

        @Schema(description = "배차 요일")
        private final DayOfWeek dispatchDay;

        @Schema(description = "배차 시간")
        private final LocalTime dispatchStartTime;

        @Schema(description = "예약 인원", example = "2")
        private final Integer reservationCount;

        @Schema(description = "예약 생성일")
        private final LocalDateTime createdAt;
    }
}
