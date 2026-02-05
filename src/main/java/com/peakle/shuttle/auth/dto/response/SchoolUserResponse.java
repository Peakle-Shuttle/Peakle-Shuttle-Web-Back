package com.peakle.shuttle.auth.dto.response;

import com.peakle.shuttle.auth.entity.User;

import java.time.LocalDateTime;

public record SchoolUserResponse(
    Long userCode,
    String userId,
    String userName,
    String userEmail,
    String userMajor,
    LocalDateTime createdAt
) {
    public static SchoolUserResponse from(User user) {
        return new SchoolUserResponse(
                user.getUserCode(),
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getUserMajor(),
                user.getCreatedAt()
        );
    }
}
