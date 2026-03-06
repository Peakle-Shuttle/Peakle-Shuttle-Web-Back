package com.peakle.shuttle.admin.course.dto.response;

import com.peakle.shuttle.course.entity.Course;
import com.peakle.shuttle.global.enums.CourseStatus;
import com.peakle.shuttle.global.enums.DispatchStatus;

import java.time.LocalDateTime;

public record AdminCourseResponse(
        Long courseCode,
        String courseName,
        Integer courseSeats,
        Integer courseDuration,
        Integer courseCost,
        String departureName,
        String departureAddress,
        String arrivalName,
        String arrivalAddress,
        CourseStatus courseStatus,
        Integer dispatchCount,
        Integer completeDispatchCount,
        LocalDateTime createdAt
) {
    public static AdminCourseResponse from(Course course) {
        int dispatchCount = course.getDispatches().size();
        int completeDispatchCount = (int) course.getDispatches().stream()
                .filter(d -> d.getDispatchStatus() == DispatchStatus.COMPLETED)
                .count();

        return new AdminCourseResponse(
                course.getCourseCode(),
                course.getCourseName(),
                course.getCourseSeats(),
                course.getCourseDuration(),
                course.getCourseCost(),
                course.getDepartureName(),
                course.getDepartureAddress(),
                course.getArrivalName(),
                course.getArrivalAddress(),
                course.getCourseStatus(),
                dispatchCount,
                completeDispatchCount,
                course.getCreatedAt()
        );
    }
}
