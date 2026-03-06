package com.peakle.shuttle.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
        @NotNull Long courseCode,
        @NotNull Long reservationCode,
        @NotNull Integer rating,
        @NotBlank String reviewContent,
        String reviewImage
) {
}
