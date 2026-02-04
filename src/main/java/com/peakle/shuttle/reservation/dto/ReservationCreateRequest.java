package com.peakle.shuttle.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationCreateRequest(
        @NotNull Long dispatchCode,
        @NotNull @Min(1) Integer count
) {
}
