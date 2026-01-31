package com.peakle.shuttle.global.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 사용자 권한 역할 (일반 사용자, 관리자) */
@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_USER("ROLE_USER", "일반 사용자"),
    ROLE_ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;

    public static Role fromKey(String key) {
        return Arrays.stream(values())
                .filter(role -> role.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + key));
    }
}
