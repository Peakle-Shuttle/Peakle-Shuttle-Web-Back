package com.peakle.shuttle.admin.course.dto.request;

public record CourseUpdateRequest(
        String courseName,
        Integer courseSeats,
        Integer courseDuration,
        Integer courseCost,
        String departureName,
        String departureAddress,
        String arrivalName,
        String arrivalAddress
) {
}
