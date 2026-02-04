package com.peakle.shuttle.admin.reservation.dto;

import com.peakle.shuttle.reservation.entity.Reservation;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AdminReservationResponse(
        Long reservationCode,
        Long userCode,
        String userName,
        String userId,
        Long dispatchCode,
        String courseName,
        LocalTime dispatchStartTime,
        DayOfWeek dispatchDay,
        Integer reservationCount,
        LocalDateTime createdAt
) {
    public static AdminReservationResponse from(Reservation reservation) {
        return new AdminReservationResponse(
                reservation.getReservationCode(),
                reservation.getUser().getUserCode(),
                reservation.getUser().getUserName(),
                reservation.getUser().getUserId(),
                reservation.getDispatch().getDispatchCode(),
                reservation.getDispatch().getCourse().getCourseName(),
                reservation.getDispatch().getDispatchStartTime(),
                reservation.getDispatch().getDispatchDay(),
                reservation.getReservationCount(),
                reservation.getCreatedAt()
        );
    }
}
