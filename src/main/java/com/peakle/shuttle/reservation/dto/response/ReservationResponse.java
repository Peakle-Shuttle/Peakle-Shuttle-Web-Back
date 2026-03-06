package com.peakle.shuttle.reservation.dto.response;

import com.peakle.shuttle.course.entity.Course;
import com.peakle.shuttle.reservation.entity.Reservation;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long reservationCode,
        Long dispatchCode,
        CourseInfo course,
        LocalDateTime dispatchDatetime,
        Integer reservationCount,
        String reservationStatus,
        LocalDateTime createdAt
) {
    public record CourseInfo(
            Long courseCode,
            String courseName,
            Integer courseSeats,
            Integer courseDuration,
            Integer courseCost,
            String departureStopName,
            String arrivalStopName
    ) {
        public static CourseInfo from(Course course) {
            String departure = course.getDepartureName();
            String arrival = course.getArrivalName();
            return new CourseInfo(
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCourseSeats(),
                    course.getCourseDuration(),
                    course.getCourseCost(),
                    departure,
                    arrival
            );
        }
    }

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getReservationCode(),
                reservation.getDispatch().getDispatchCode(),
                CourseInfo.from(reservation.getDispatch().getCourse()),
                reservation.getDispatch().getDispatchDatetime(),
                reservation.getReservationCount(),
                reservation.getReservationStatus().name(),
                reservation.getCreatedAt()
        );
    }
}
