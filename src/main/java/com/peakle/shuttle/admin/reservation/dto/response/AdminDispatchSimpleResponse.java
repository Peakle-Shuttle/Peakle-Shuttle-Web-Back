package com.peakle.shuttle.admin.reservation.dto.response;

import com.peakle.shuttle.course.entity.Dispatch;

import java.time.LocalDateTime;

public record AdminDispatchSimpleResponse(
        Long dispatchCode,
        LocalDateTime dispatchDatetime
) {
    public static AdminDispatchSimpleResponse from(Dispatch dispatch) {
        return new AdminDispatchSimpleResponse(
                dispatch.getDispatchCode(),
                dispatch.getDispatchDatetime()
        );
    }
}
