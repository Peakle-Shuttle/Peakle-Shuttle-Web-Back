package com.peakle.shuttle.course.dto.response;

import com.peakle.shuttle.course.entity.Course;
import com.peakle.shuttle.course.entity.CourseStop;
import com.peakle.shuttle.course.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.List;

public record CourseDetailResponse(
        Long courseCode,
        String courseName,
        Integer courseSeats,
        Integer courseDuration,
        Integer courseCost,
        List<StopInfo> stops,
        List<DispatchInfo> dispatches
) {
    public record StopInfo(
            Long stopCode,
            String stopName,
            String stopAddress,
            int stopOrder,
            Integer estimatedArrival
    ) {
        public static StopInfo from(CourseStop cs) {
            return new StopInfo(
                    cs.getStop().getStopCode(),
                    cs.getStop().getStopName(),
                    cs.getStop().getStopAddress(),
                    cs.getStopOrder(),
                    cs.getEstimatedArrival()
            );
        }
    }

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

    public static CourseDetailResponse from(Course course) {
        List<StopInfo> stopInfos = course.getCourseStops().stream()
                .map(StopInfo::from)
                .toList();

        List<DispatchInfo> dispatchInfos = course.getDispatches().stream()
                .map(d -> DispatchInfo.from(d, course.getCourseSeats()))
                .toList();

        return new CourseDetailResponse(
                course.getCourseCode(),
                course.getCourseName(),
                course.getCourseSeats(),
                course.getCourseDuration(),
                course.getCourseCost(),
                stopInfos,
                dispatchInfos
        );
    }
}
