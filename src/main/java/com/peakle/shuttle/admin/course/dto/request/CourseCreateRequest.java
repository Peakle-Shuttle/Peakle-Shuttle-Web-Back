package com.peakle.shuttle.admin.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CourseCreateRequest(
        @NotBlank String courseName,
        @NotNull Integer courseSeats,
        @NotNull Integer courseDuration,
        @NotNull Integer courseCost,
        List<CourseStopRequest> stops
) {
}
