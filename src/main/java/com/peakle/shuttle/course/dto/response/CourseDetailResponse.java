package com.peakle.shuttle.course.dto.response;

import com.peakle.shuttle.course.entity.Course;
import com.peakle.shuttle.course.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.List;

public record CourseDetailResponse(
        Long courseCode,
        String courseName,
        Integer courseSeats,
        Integer courseDuration,
        Integer courseCost,
        String departureStopName,
        String arrivalStopName,
        List<DispatchInfo> dispatches,
        Boolean wished
) {
    public record DispatchInfo(
            Long dispatchCode,
            LocalDateTime dispatchDatetime,
            Integer availableSeats
    ) {
        public static DispatchInfo from(Dispatch dispatch, Integer totalSeats) {
            Integer occupied = dispatch.getDispatchOccupied() != null ? dispatch.getDispatchOccupied() : 0;
            return new DispatchInfo(
                    dispatch.getDispatchCode(),
                    dispatch.getDispatchDatetime(),
                    totalSeats - occupied
            );
        }
    }

    public static CourseDetailResponse from(Course course, boolean wished) {
        String departure = course.getDepartureName();
        String arrival = course.getArrivalName();

        List<DispatchInfo> dispatchInfos = course.getDispatches().stream()
                .map(d -> DispatchInfo.from(d, course.getCourseSeats()))
                .toList();

        return new CourseDetailResponse(
                course.getCourseCode(),
                course.getCourseName(),
                course.getCourseSeats(),
                course.getCourseDuration(),
                course.getCourseCost(),
                departure,
                arrival,
                dispatchInfos,
                wished
        );
    }
}
