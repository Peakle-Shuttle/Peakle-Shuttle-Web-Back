package com.peakle.shuttle.course.dto.request;

import jakarta.validation.constraints.NotNull;

public record WishCreateRequest(
        @NotNull Long courseCode
) {
}
