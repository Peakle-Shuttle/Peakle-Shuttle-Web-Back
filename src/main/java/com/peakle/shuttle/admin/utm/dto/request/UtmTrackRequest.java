package com.peakle.shuttle.admin.utm.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UtmTrackRequest(
        @NotBlank(message = "utm_source는 필수입니다.")
        String utmSource,

        @NotBlank(message = "utm_medium은 필수입니다.")
        String utmMedium,

        @NotBlank(message = "utm_campaign은 필수입니다.")
        String utmCampaign
) {
}
