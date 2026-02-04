package com.peakle.shuttle.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationUpdateRequest(
        @NotNull Long reservationCode,
        @NotNull @Min(1) Integer count
) {
}
