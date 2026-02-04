package com.peakle.shuttle.admin.course.dto;

import jakarta.validation.constraints.NotNull;

public record CourseStopRequest(
        Long stopCode,
        String stopName,
        String stopAddress,
        @NotNull Integer stopOrder,
        Integer estimatedArrival
) {
}
