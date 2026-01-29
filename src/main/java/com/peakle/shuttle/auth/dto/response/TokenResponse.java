package com.peakle.shuttle.auth.dto.response;

import lombok.Builder;

/** Access/Refresh 토큰 응답 DTO */
@Builder
public record TokenResponse (String accessToken, String refreshToken) {}
