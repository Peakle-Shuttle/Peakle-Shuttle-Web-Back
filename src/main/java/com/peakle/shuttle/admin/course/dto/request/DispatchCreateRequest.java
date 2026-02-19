package com.peakle.shuttle.admin.course.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DispatchCreateRequest(
        @NotNull Long courseCode,
        @NotNull LocalDateTime dispatchDatetime
) {
}
