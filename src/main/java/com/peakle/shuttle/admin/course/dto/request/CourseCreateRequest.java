package com.peakle.shuttle.admin.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseCreateRequest(
        @NotNull Long schoolCode,
        @NotBlank String courseName,
        @NotNull Integer courseSeats,
        @NotNull Integer courseDuration,
        @NotNull Integer courseCost,
        @NotBlank String departureName,
        @NotBlank String departureAddress,
        @NotBlank String arrivalName,
        @NotBlank String arrivalAddress
) {
}
