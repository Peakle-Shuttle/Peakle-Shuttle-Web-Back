package com.peakle.shuttle.open.dto;

import jakarta.validation.constraints.NotNull;

public record OpenUpdateRequest(
        @NotNull Long openCode,
        String openContent
) {
}
