package com.peakle.shuttle.auth.dto.response;

import com.peakle.shuttle.global.enums.AuthProvider;
import com.peakle.shuttle.global.enums.Role;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UserClientResponse (
    Long userCode,
    String userId,
    String userName,
    Role userRole,
    String userGender,
    String userNumber,
    LocalDate userBirth,
    String userSchool,
    String userMajor,
    AuthProvider provider,
    LocalDateTime createAt,
    LocalDateTime updatedAt
    ) {
}
