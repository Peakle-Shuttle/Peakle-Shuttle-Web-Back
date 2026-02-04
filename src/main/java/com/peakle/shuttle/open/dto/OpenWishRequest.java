package com.peakle.shuttle.open.dto;

import jakarta.validation.constraints.NotNull;

public record OpenWishRequest(
        @NotNull Long openCode
) {
}
