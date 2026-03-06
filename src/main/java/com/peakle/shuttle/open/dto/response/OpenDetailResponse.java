package com.peakle.shuttle.open.dto.response;

import com.peakle.shuttle.open.entity.Open;

import java.time.LocalDateTime;

public record OpenDetailResponse(
        Long openCode,
        Long userCode,
        String openTitle,
        String openContent,
        Integer openViewCount,
        Long wishCount,
        Boolean wished,
        LocalDateTime createdAt
) {
    public static OpenDetailResponse of(Open open, Long wishCount, Boolean wished) {
        return new OpenDetailResponse(
                open.getOpenCode(),
                open.getUser().getUserCode(),
                open.getOpenTitle(),
                open.getOpenContent(),
                open.getOpenViewCount(),
                wishCount,
                wished,
                open.getCreatedAt()
        );
    }
}
