package com.peakle.shuttle.admin.course.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DispatchUpdateRequest(
        @NotNull Long dispatchCode,
        LocalDateTime dispatchDatetime
) {
}
