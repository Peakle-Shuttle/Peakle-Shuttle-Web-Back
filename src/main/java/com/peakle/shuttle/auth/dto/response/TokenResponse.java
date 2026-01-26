package com.peakle.shuttle.auth.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse (String accessToken, String refreshToken) {}
