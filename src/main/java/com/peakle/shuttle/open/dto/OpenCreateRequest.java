package com.peakle.shuttle.open.dto;

import jakarta.validation.constraints.NotBlank;

public record OpenCreateRequest(
        @NotBlank String openContent
) {
}
