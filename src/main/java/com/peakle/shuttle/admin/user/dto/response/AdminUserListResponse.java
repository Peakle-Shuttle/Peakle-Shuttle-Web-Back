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
        LocalDateTime createdAt,
        Long qnaCount,
        Long qnaCompletedCount,
        Long matchingCount,
        Long matchingCompletedCount,
        Long reservationCount,
        Long totalPurchaseAmount
) {
    public static AdminUserListResponse of(User user, Long qnaCount, Long qnaCompletedCount,
                                           Long matchingCount, Long matchingCompletedCount,
                                           Long reservationCount, Long totalPurchaseAmount) {
        return new AdminUserListResponse(
                user.getUserCode(),
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getSchool() != null ? user.getSchool().getSchoolName() : null,
                user.getUserMajor(),
                user.getUserGender(),
                user.getUserStatus().name(),
                user.getCreatedAt(),
                qnaCount,
                qnaCompletedCount,
                matchingCount,
                matchingCompletedCount,
                reservationCount,
                totalPurchaseAmount
        );
    }
}
