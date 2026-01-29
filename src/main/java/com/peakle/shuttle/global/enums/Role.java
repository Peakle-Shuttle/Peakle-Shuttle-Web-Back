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

    /**
     * 주어진 키에 대응하는 Role을 반환한다.
     *
     * @param key 역할 키(예: "ROLE_USER", "ROLE_ADMIN")
     * @return `key`에 해당하는 Role 객체
     * @throws IllegalArgumentException 등록되지 않은 키가 전달된 경우
     */
    public static Role fromKey(String key) {
        return Arrays.stream(values())
                .filter(role -> role.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + key));
    }
}