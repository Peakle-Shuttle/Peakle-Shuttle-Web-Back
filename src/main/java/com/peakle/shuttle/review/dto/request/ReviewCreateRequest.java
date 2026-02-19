package com.peakle.shuttle.review.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
        @NotNull Long courseCode,
        @NotBlank String reviewContent,
        String reviewImage
) {
}
