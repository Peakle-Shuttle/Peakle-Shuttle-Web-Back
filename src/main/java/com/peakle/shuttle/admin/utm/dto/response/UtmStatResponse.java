package com.peakle.shuttle.admin.utm.dto.response;

import com.peakle.shuttle.admin.utm.entity.UtmStat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "UTM 통계 응답")
public class UtmStatResponse {

    @Schema(description = "UTM ID", example = "1")
    private final Long utmId;

    @Schema(description = "UTM 소스", example = "google")
    private final String utmSource;

    @Schema(description = "UTM 매체", example = "cpc")
    private final String utmMedium;

    @Schema(description = "UTM 캠페인", example = "winter_sale")
    private final String utmCampaign;

    @Schema(description = "클릭 수", example = "150")
    private final Long utmCount;

    @Schema(description = "생성 시간", example = "2025-02-16T10:30:00")
    private final LocalDateTime createdAt;

    @Schema(description = "수정 시간", example = "2025-02-16T15:45:00")
    private final LocalDateTime updatedAt;

    public static UtmStatResponse from(UtmStat utmStat) {
        return UtmStatResponse.builder()
                .utmId(utmStat.getUtmId())
                .utmSource(utmStat.getUtmSource())
                .utmMedium(utmStat.getUtmMedium())
                .utmCampaign(utmStat.getUtmCampaign())
                .utmCount(utmStat.getUtmCount())
                .createdAt(utmStat.getCreatedAt())
                .updatedAt(utmStat.getUpdatedAt())
                .build();
    }
}
