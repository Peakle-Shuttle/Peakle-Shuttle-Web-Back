package com.peakle.shuttle.admin.visitor.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "방문자 통계 응답")
public class VisitorStatResponse {

    @Schema(description = "날짜", example = "2025-02-12")
    private final String date;

    @Schema(description = "순 방문자 수 (UV)", example = "150")
    private final Long uniqueVisitors;

    @Schema(description = "페이지 조회 수 (PV)", example = "430")
    private final Long pageViews;

    public static VisitorStatResponse of(String date, Long uv, Long pv) {
        return VisitorStatResponse.builder()
                .date(date)
                .uniqueVisitors(uv)
                .pageViews(pv)
                .build();
    }
}
