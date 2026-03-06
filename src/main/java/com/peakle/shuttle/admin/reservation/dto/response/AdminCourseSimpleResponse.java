package com.peakle.shuttle.admin.reservation.dto.response;

import com.peakle.shuttle.course.entity.Course;

public record AdminCourseSimpleResponse(
        Long courseCode,
        String courseName
) {
    public static AdminCourseSimpleResponse from(Course course) {
        return new AdminCourseSimpleResponse(
                course.getCourseCode(),
                course.getCourseName()
        );
    }
}
