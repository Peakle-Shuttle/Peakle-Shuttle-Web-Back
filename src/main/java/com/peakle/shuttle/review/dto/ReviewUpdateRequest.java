package com.peakle.shuttle.review.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewUpdateRequest(
        @NotNull Long reviewCode,
        String reviewContent,
        String reviewImage
) {
}
