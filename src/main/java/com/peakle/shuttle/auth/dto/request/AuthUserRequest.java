package com.peakle.shuttle.auth.dto.request;

import com.peakle.shuttle.global.enums.Role;
import jakarta.validation.constraints.NotNull;

/** 인증된 사용자 정보 DTO (code, role) */
public record AuthUserRequest (
        @NotNull Long code,
        @NotNull Role role
) {
        /**
         * Role 열거형에서 해당 사용자의 권한 키 문자열을 반환한다.
         *
         * @return `Role`에 해당하는 권한 키 문자열 (예: 보안 컨텍스트에 사용되는 키)
         */
        public String securityRole() {
            return role.getKey();
        }
}