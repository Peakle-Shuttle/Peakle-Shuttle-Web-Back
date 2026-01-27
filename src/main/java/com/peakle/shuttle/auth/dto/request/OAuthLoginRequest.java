package com.peakle.shuttle.auth.dto.request;

import com.peakle.shuttle.global.enums.AuthProvider;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OAuthLoginRequest(
    @NotNull(message = "KAKAO, GOOGLE 등 Provider를 입력해주세요")
    AuthProvider authProvider,
    @NotBlank(message = "간편 로그인을 진행하여 받은 Token을 입력해주세요")
    String providerToken
) {}
