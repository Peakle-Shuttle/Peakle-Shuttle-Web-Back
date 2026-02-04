package com.peakle.shuttle.course.dto;

import jakarta.validation.constraints.NotNull;

public record WishCreateRequest(
        @NotNull Long courseCode
) {
}
