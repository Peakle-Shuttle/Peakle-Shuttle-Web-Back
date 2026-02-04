package com.peakle.shuttle.admin.course.dto;

import com.peakle.shuttle.course.entity.Course;
import com.peakle.shuttle.course.entity.CourseStop;

import java.time.LocalDateTime;
import java.util.List;

public record AdminCourseResponse(
        Long courseCode,
        String courseName,
        Integer courseSeats,
        Integer courseDuration,
        Integer courseCost,
        List<CourseStopInfo> stops,
        LocalDateTime createdAt
) {
    public record CourseStopInfo(
            Long stopCode,
            String stopName,
            String stopAddress,
            int stopOrder,
            Integer estimatedArrival
    ) {
        public static CourseStopInfo from(CourseStop cs) {
            return new CourseStopInfo(
                    cs.getStop().getStopCode(),
                    cs.getStop().getStopName(),
                    cs.getStop().getStopAddress(),
                    cs.getStopOrder(),
                    cs.getEstimatedArrival()
            );
        }
    }

    public static AdminCourseResponse from(Course course) {
        List<CourseStopInfo> stops = course.getCourseStops().stream()
                .map(CourseStopInfo::from)
                .toList();

        return new AdminCourseResponse(
                course.getCourseCode(),
                course.getCourseName(),
                course.getCourseSeats(),
                course.getCourseDuration(),
                course.getCourseCost(),
                stops,
                course.getCreatedAt()
        );
    }
}
