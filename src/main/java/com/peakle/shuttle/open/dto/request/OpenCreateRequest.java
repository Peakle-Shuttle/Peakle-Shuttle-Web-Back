package com.peakle.shuttle.open.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OpenCreateRequest(
        @NotBlank String openContent
) {
}
