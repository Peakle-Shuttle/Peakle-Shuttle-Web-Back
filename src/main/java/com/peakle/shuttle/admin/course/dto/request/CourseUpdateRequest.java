package com.peakle.shuttle.admin.course.dto.request;

import java.util.List;

public record CourseUpdateRequest(
        String courseName,
        Integer courseSeats,
        Integer courseDuration,
        Integer courseCost,
        List<CourseStopRequest> stops
) {
}
