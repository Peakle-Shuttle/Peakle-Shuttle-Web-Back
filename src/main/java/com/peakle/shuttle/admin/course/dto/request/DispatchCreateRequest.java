package com.peakle.shuttle.admin.course.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DispatchCreateRequest(
        @NotNull Long courseCode,
        @NotNull LocalTime dispatchStartTime,
        @NotNull DayOfWeek dispatchDay
) {
}
