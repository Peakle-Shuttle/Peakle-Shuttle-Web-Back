package com.peakle.shuttle.open.dto.response;

import com.peakle.shuttle.open.entity.Open;

import java.time.LocalDateTime;

public record OpenListResponse(
        Long openCode,
        Long userCode,
        String openTitle,
        Integer openViewCount,
        LocalDateTime createdAt,
        Long wishCount,
        Boolean wished
) {
    public static OpenListResponse from(Open open, Long wishCount, Boolean wished) {
        return new OpenListResponse(
                open.getOpenCode(),
                open.getUser().getUserCode(),
                open.getOpenTitle(),
                open.getOpenViewCount(),
                open.getCreatedAt(),
                wishCount,
                wished
        );
    }
}
