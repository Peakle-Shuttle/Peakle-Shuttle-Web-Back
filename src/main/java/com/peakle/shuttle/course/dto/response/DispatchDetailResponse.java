package com.peakle.shuttle.course.dto.response;

import com.peakle.shuttle.course.entity.Dispatch;

import java.time.LocalDateTime;

public record DispatchDetailResponse(
        Long dispatchCode,
        Long courseCode,
        String courseName,
        LocalDateTime dispatchDatetime,
        Integer totalSeats,
        Integer occupiedSeats,
        Integer availableSeats,
        Integer courseCost,
        String departureStopName,
        String arrivalStopName
) {
    public static DispatchDetailResponse from(Dispatch dispatch) {
        Integer totalSeats = dispatch.getCourse().getCourseSeats();
        Integer occupied = dispatch.getDispatchOccupied() != null ? dispatch.getDispatchOccupied() : 0;

        String departure = dispatch.getCourse().getDepartureName();
        String arrival = dispatch.getCourse().getArrivalName();

        return new DispatchDetailResponse(
                dispatch.getDispatchCode(),
                dispatch.getCourse().getCourseCode(),
                dispatch.getCourse().getCourseName(),
                dispatch.getDispatchDatetime(),
                totalSeats,
                occupied,
                totalSeats - occupied,
                dispatch.getCourse().getCourseCost(),
                departure,
                arrival
        );
    }
}
