package com.peakle.shuttle.admin.course.dto;

import com.peakle.shuttle.course.entity.Dispatch;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AdminDispatchResponse(
        Long dispatchCode,
        Long courseCode,
        String courseName,
        LocalTime dispatchStartTime,
        DayOfWeek dispatchDay,
        Integer dispatchWishCount,
        Integer dispatchOccupied
) {
    public static AdminDispatchResponse from(Dispatch dispatch) {
        return new AdminDispatchResponse(
                dispatch.getDispatchCode(),
                dispatch.getCourse().getCourseCode(),
                dispatch.getCourse().getCourseName(),
                dispatch.getDispatchStartTime(),
                dispatch.getDispatchDay(),
                dispatch.getDispatchWishCount(),
                dispatch.getDispatchOccupied()
        );
    }
}
