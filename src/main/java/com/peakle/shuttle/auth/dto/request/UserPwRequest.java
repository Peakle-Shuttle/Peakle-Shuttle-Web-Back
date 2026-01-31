package com.peakle.shuttle.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/** 비밀번호 변경 요청 DTO (기존 비밀번호 + 새 비밀번호) */
public record UserPwRequest(
        @NotBlank String oldPassword,
        @NotBlank String newPassword
) {}
