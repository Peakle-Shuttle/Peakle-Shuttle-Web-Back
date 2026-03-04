package com.peakle.shuttle.admin.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "재구매자 비중 일별 추이 그래프 응답")
public class RepeatBuyerRatioTrendResponse {

    @Schema(description = "일별 재구매자 비중 데이터")
    private final List<Entry> data;

    @Getter
    @Builder
    @Schema(description = "일별 재구매자 비중 항목")
    public static class Entry {

        @Schema(description = "날짜", example = "01.10")
        private final String date;

        @Schema(description = "최초 구매자 비율 (%)", example = "80")
        private final Integer first;

        @Schema(description = "재구매자 비율 (%)", example = "20")
        private final Integer re;
    }
}
