package com.peakle.shuttle.review.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReviewUpdateRequest(
        @NotNull Long reviewCode,
        String reviewContent,
        String reviewImage
) {
}
