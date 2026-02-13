package com.peakle.shuttle.open.dto.request;

import jakarta.validation.constraints.NotNull;

public record OpenWishRequest(
        @NotNull Long openCode
) {
}
