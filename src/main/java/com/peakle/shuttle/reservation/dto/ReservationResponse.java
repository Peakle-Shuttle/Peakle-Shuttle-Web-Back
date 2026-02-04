package com.peakle.shuttle.reservation.dto;

import com.peakle.shuttle.reservation.entity.Reservation;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReservationResponse(
        Long reservationCode,
        Long dispatchCode,
        String courseName,
        LocalTime dispatchStartTime,
        DayOfWeek dispatchDay,
        Integer reservationCount,
        LocalDateTime createdAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getReservationCode(),
                reservation.getDispatch().getDispatchCode(),
                reservation.getDispatch().getCourse().getCourseName(),
                reservation.getDispatch().getDispatchStartTime(),
                reservation.getDispatch().getDispatchDay(),
                reservation.getReservationCount(),
                reservation.getCreatedAt()
        );
    }
}
