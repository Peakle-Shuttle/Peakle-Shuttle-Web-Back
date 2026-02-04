package com.peakle.shuttle.admin.user.dto;

import com.peakle.shuttle.auth.entity.User;

import java.time.LocalDateTime;

public record AdminUserListResponse(
        Long userCode,
        String userId,
        String userName,
        String userEmail,
        String userSchool,
        String userMajor,
        String userGender,
        String status,
        LocalDateTime createdAt
) {
    public static AdminUserListResponse from(User user) {
        return new AdminUserListResponse(
                user.getUserCode(),
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getUserSchool(),
                user.getUserMajor(),
                user.getUserGender(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }
}
