package com.peakle.shuttle.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/** 카카오 provider ID 존재 여부 확인 요청 DTO */
public record ProviderCheckRequest(
    @NotBlank(message = "카카오 토큰을 입력해주세요.")
    String providerToken
) {}
