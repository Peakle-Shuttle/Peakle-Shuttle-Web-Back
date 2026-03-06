package com.peakle.shuttle.admin.stats.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "성과 분석 통합 응답")
public class PerformanceStatsResponse {

    @Schema(description = "재구매 매출")
    private final RevenueData repeatRevenue;

    @Schema(description = "재구매자 비중 추이")
    private final List<BuyerRatioEntry> repeatBuyerRatio;

    @Schema(description = "재구매 확률 추이")
    private final List<ProbabilityEntry> repurchaseProbability;

    @Schema(description = "재구매 주기 추이")
    private final List<CycleEntry> repurchaseCycle;

    @Schema(description = "충성고객 비중 추이")
    private final List<LoyaltyEntry> loyalCustomerDistribution;

    @Getter
    @Builder
    @Schema(description = "재구매 매출 데이터")
    public static class RevenueData {
        @Schema(description = "총 매출", example = "1500000")
        private final Long totalRevenue;

        @Schema(description = "재구매 매출", example = "1200000")
        private final Long repeatRevenue;

        @Schema(description = "재구매 매출 비율 (%)", example = "80.0")
        private final Double ratio;
    }

    @Getter
    @Builder
    @Schema(description = "재구매자 비중 항목")
    public static class BuyerRatioEntry {
        @Schema(description = "날짜", example = "01.01")
        private final String date;

        @Schema(description = "최초 구매자 비율 (%)", example = "80")
        private final Integer first;

        @Schema(description = "재구매자 비율 (%)", example = "20")
        private final Integer re;
    }

    @Getter
    @Builder
    @Schema(description = "재구매 확률 항목")
    public static class ProbabilityEntry {
        @Schema(description = "날짜", example = "01.01")
        private final String date;

        @Schema(description = "재구매 확률 (%)", example = "28")
        private final Integer probability;
    }

    @Getter
    @Builder
    @Schema(description = "재구매 주기 항목")
    public static class CycleEntry {
        @Schema(description = "날짜", example = "01.01")
        private final String date;

        @Schema(description = "평균 재구매 주기 (일)", example = "12")
        private final Integer averageDays;
    }

    @Getter
    @Builder
    @Schema(description = "충성고객 비중 항목")
    public static class LoyaltyEntry {
        @Schema(description = "날짜", example = "01.01")
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
