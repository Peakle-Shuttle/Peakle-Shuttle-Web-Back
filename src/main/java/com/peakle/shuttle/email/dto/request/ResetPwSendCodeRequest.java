package com.peakle.shuttle.email.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPwSendCodeRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        String userName,

        @NotBlank(message = "전화번호를 입력해주세요.")
        String userNumber,

        @NotBlank(message = "아이디를 입력해주세요.")
        String userId,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식을 입력해주세요.")
        String email
) {}
