package com.peakle.shuttle.course.dto.response;

import com.peakle.shuttle.course.entity.CourseStop;
import com.peakle.shuttle.course.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.List;

public record DispatchDetailResponse(
        Long dispatchCode,
        Long courseCode,
        String courseName,
        LocalDateTime dispatchDatetime,
        Integer totalSeats,
        Integer occupiedSeats,
        Integer availableSeats,
        Integer courseCost,
        List<StopInfo> stops
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

    public static DispatchDetailResponse from(Dispatch dispatch) {
        Integer totalSeats = dispatch.getCourse().getCourseSeats();
        Integer occupied = dispatch.getDispatchOccupied() != null ? dispatch.getDispatchOccupied() : 0;

        List<StopInfo> stopInfos = dispatch.getCourse().getCourseStops().stream()
                .map(StopInfo::from)
                .toList();

        return new DispatchDetailResponse(
                dispatch.getDispatchCode(),
                dispatch.getCourse().getCourseCode(),
                dispatch.getCourse().getCourseName(),
                dispatch.getDispatchDatetime(),
                totalSeats,
                occupied,
                totalSeats - occupied,
                dispatch.getCourse().getCourseCost(),
                stopInfos
        );
    }
}
