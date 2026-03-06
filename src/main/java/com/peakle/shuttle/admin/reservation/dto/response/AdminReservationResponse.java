package com.peakle.shuttle.admin.reservation.dto.response;

import com.peakle.shuttle.reservation.entity.Reservation;

import java.time.LocalDateTime;

public record AdminReservationResponse(
        Long reservationCode,
        Long userCode,
        String userName,
        String userId,
        Long dispatchCode,
        Long courseCode,
        String courseName,
        LocalDateTime dispatchDatetime,
        Integer reservationCount,
        String reservationStatus,
        LocalDateTime createdAt
) {
    public static AdminReservationResponse from(Reservation reservation) {
        return new AdminReservationResponse(
                reservation.getReservationCode(),
                reservation.getUser().getUserCode(),
                reservation.getUser().getUserName(),
                reservation.getUser().getUserId(),
                reservation.getDispatch().getDispatchCode(),
                reservation.getDispatch().getCourse().getCourseCode(),
                reservation.getDispatch().getCourse().getCourseName(),
                reservation.getDispatch().getDispatchDatetime(),
                reservation.getReservationCount(),
                reservation.getReservationStatus().name(),
                reservation.getCreatedAt()
        );
    }
}
