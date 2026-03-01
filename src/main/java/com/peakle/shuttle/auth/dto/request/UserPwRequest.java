package com.peakle.shuttle.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/** 비밀번호 재설정 요청 DTO */
public record UserPwRequest(
        @NotBlank String newPassword
) {}
