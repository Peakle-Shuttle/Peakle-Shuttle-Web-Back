package com.peakle.shuttle.course.dto.response;

import com.peakle.shuttle.school.entity.School;

import java.util.List;

public record SchoolWithCoursesResponse(
    Long schoolCode,
    String schoolName,
    String schoolAddress,
    List<CourseListResponse> courses
) {
    public static SchoolWithCoursesResponse of(School school, List<CourseListResponse> courses) {
        return new SchoolWithCoursesResponse(
                school.getSchoolCode(),
                school.getSchoolName(),
                school.getSchoolAddress(),
                courses
        );
    }
}
