package com.peakle.shuttle.open.dto.response;

import com.peakle.shuttle.open.entity.Open;

import java.time.LocalDateTime;

public record OpenListResponse(
        Long openCode,
        String openContent,
        LocalDateTime createdAt,
        Long wishCount,
        Boolean wished
) {
    public static OpenListResponse from(Open open, Long wishCount, Boolean wished) {
        return new OpenListResponse(
                open.getOpenCode(),
                open.getOpenContent(),
                open.getCreatedAt(),
                wishCount,
                wished
        );
    }
}
