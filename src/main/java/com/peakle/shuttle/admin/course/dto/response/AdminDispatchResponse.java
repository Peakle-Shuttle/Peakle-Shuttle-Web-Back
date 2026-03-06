package com.peakle.shuttle.admin.course.dto.response;

import com.peakle.shuttle.course.entity.Dispatch;
import com.peakle.shuttle.global.enums.DispatchStatus;

import java.time.LocalDateTime;

public record AdminDispatchResponse(
        Long dispatchCode,
        Long courseCode,
        String courseName,
        LocalDateTime dispatchDatetime,
        Integer dispatchWishCount,
        Integer dispatchOccupied,
        DispatchStatus dispatchStatus
) {
    public static AdminDispatchResponse from(Dispatch dispatch) {
        return new AdminDispatchResponse(
                dispatch.getDispatchCode(),
                dispatch.getCourse().getCourseCode(),
                dispatch.getCourse().getCourseName(),
                dispatch.getDispatchDatetime(),
                dispatch.getDispatchWishCount(),
                dispatch.getDispatchOccupied(),
                dispatch.getDispatchStatus()
        );
    }
}
