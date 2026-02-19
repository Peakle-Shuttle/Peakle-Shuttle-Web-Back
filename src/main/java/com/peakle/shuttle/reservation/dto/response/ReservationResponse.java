package com.peakle.shuttle.reservation.dto.response;

import com.peakle.shuttle.reservation.entity.Reservation;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long reservationCode,
        Long dispatchCode,
        String courseName,
        LocalDateTime dispatchDatetime,
        Integer reservationCount,
        LocalDateTime createdAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getReservationCode(),
                reservation.getDispatch().getDispatchCode(),
                reservation.getDispatch().getCourse().getCourseName(),
                reservation.getDispatch().getDispatchDatetime(),
                reservation.getReservationCount(),
                reservation.getCreatedAt()
        );
    }
}
