package com.peakle.shuttle.course.dto;

import com.peakle.shuttle.course.entity.Course;
import com.peakle.shuttle.course.entity.CourseStop;
import com.peakle.shuttle.course.entity.Dispatch;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public record CourseListResponse(
        Long courseCode,
        String courseName,
        Integer courseSeats,
        Integer courseDuration,
        Integer courseCost,
        String departureStopName,
        String arrivalStopName,
        List<DispatchInfo> dispatches
) {
    public record DispatchInfo(
            Long dispatchCode,
            LocalTime dispatchStartTime,
            DayOfWeek dispatchDay,
            Integer availableSeats
    ) {
        public static DispatchInfo from(Dispatch dispatch, Integer totalSeats) {
            Integer occupied = dispatch.getDispatchOccupied() != null ? dispatch.getDispatchOccupied() : 0;
            return new DispatchInfo(
                    dispatch.getDispatchCode(),
                    dispatch.getDispatchStartTime(),
                    dispatch.getDispatchDay(),
                    totalSeats - occupied
            );
        }
    }

    public static CourseListResponse from(Course course) {
        String departure = course.getDepartureStop() != null ? course.getDepartureStop().getStopName() : null;
        String arrival = course.getArrivalStop() != null ? course.getArrivalStop().getStopName() : null;

        List<DispatchInfo> dispatchInfos = course.getDispatches().stream()
                .map(d -> DispatchInfo.from(d, course.getCourseSeats()))
                .toList();

        return new CourseListResponse(
                course.getCourseCode(),
                course.getCourseName(),
                course.getCourseSeats(),
                course.getCourseDuration(),
                course.getCourseCost(),
                departure,
                arrival,
                dispatchInfos
        );
    }
}
