package com.peakle.shuttle.admin.reservation.dto;

import jakarta.validation.constraints.NotNull;

public record ReservationUpdateRequest(
        @NotNull Integer reservationCount
) {
}
