package com.peakle.shuttle.admin.user.dto.response;

import com.peakle.shuttle.auth.entity.User;

import java.time.LocalDateTime;

public record AdminUserListResponse(
        Long userCode,
        String userId,
        String userName,
        String userEmail,
        String schoolName,
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
                user.getSchool() != null ? user.getSchool().getSchoolName() : null,
                user.getUserMajor(),
                user.getUserGender(),
                user.getUserStatus().name(),
                user.getCreatedAt()
        );
    }
}
