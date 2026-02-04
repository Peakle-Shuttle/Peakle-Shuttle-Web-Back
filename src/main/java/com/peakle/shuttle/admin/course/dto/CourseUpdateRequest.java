package com.peakle.shuttle.admin.course.dto;

import java.util.List;

public record CourseUpdateRequest(
        String courseName,
        Integer courseSeats,
        Integer courseDuration,
        Integer courseCost,
        List<CourseStopRequest> stops
) {
}
