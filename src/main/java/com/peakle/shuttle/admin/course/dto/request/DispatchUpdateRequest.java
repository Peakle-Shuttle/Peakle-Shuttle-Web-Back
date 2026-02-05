package com.peakle.shuttle.admin.course.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DispatchUpdateRequest(
        @NotNull Long dispatchCode,
        LocalTime dispatchStartTime,
        DayOfWeek dispatchDay
) {
}
