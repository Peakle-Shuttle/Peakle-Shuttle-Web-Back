package com.peakle.shuttle.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserPwRequest(
        @NotBlank String oldPassword,
        @NotBlank String newPassword
) {}
