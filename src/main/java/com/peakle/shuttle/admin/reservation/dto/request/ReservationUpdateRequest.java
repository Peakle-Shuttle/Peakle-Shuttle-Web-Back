package com.peakle.shuttle.admin.reservation.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReservationUpdateRequest(
        @NotNull Integer reservationCount
) {
}
