package com.peakle.shuttle.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** 사용자 이메일 변경 요청 DTO */
public record UserEmailRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식을 입력해주세요.")
        String userEmail
) {}
