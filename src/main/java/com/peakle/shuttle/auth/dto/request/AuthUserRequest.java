package com.peakle.shuttle.auth.dto.request;

import com.peakle.shuttle.global.enums.Role;
import jakarta.validation.constraints.NotNull;

/** 인증된 사용자 정보 DTO (code, role) */
public record AuthUserRequest (
        @NotNull Long code,
        @NotNull Role role
) {
        public String securityRole() {
            return role.getKey();
        }
}
