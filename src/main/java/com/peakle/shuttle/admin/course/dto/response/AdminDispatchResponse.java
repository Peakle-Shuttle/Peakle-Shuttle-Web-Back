package com.peakle.shuttle.admin.course.dto.response;

import com.peakle.shuttle.course.entity.Dispatch;

import java.time.LocalDateTime;

public record AdminDispatchResponse(
        Long dispatchCode,
        Long courseCode,
        String courseName,
        LocalDateTime dispatchDatetime,
        Integer dispatchWishCount,
        Integer dispatchOccupied
) {
    public static AdminDispatchResponse from(Dispatch dispatch) {
        return new AdminDispatchResponse(
                dispatch.getDispatchCode(),
                dispatch.getCourse().getCourseCode(),
                dispatch.getCourse().getCourseName(),
                dispatch.getDispatchDatetime(),
                dispatch.getDispatchWishCount(),
                dispatch.getDispatchOccupied()
        );
    }
}
