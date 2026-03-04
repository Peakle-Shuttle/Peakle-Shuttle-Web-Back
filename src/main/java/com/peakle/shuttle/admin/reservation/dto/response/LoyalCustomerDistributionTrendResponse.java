package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "충성고객 비중 일별 분포 그래프 응답")
public class LoyalCustomerDistributionTrendResponse {

    @Schema(description = "일별 충성고객 분포 데이터")
    private final List<Entry> data;

    @Getter
    @Builder
    @Schema(description = "일별 충성고객 분포 항목")
    public static class Entry {

        @Schema(description = "날짜", example = "01.10")
        private final String date;

        @Schema(description = "1회 구매 비율 (%)", example = "90")
        private final Integer lv1;

        @Schema(description = "2회 구매 비율 (%)", example = "5")
        private final Integer lv2;

        @Schema(description = "3회 구매 비율 (%)", example = "2")
        private final Integer lv3;

        @Schema(description = "4회 구매 비율 (%)", example = "2")
        private final Integer lv4;

        @Schema(description = "5회 이상 구매 비율 (%)", example = "1")
        private final Integer lv5;
    }
}
