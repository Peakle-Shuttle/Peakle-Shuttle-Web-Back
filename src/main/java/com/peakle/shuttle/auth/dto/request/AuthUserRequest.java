package com.peakle.shuttle.auth.dto.request;

import com.peakle.shuttle.global.enums.Role;
import jakarta.validation.constraints.NotNull;

public record AuthUserRequest (
        @NotNull Long code,
        @NotNull Role role
) {
        public String securityRole() {
            return role.getKey();
        }
}
