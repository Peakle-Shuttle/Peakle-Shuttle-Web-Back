package com.peakle.shuttle.global.enums.logging;

import lombok.Getter;

/** 구조화 로깅에 사용되는 키 정의 */
@Getter
public enum LoggerKey {
    REQUEST_ID("requestId"),
    USER_INFO("userInfo"),

    EXECUTION_TIME("executionTime"),
    SUCCESS("success"),
    ERROR_MESSAGE("errorMessage"),
    STATUS("status"),
    STACK_TRACE("stackTrace"),
    QUERY("query");

    private final String key;

    /**
     * 열거형 상수에 연결된 로그 키 문자열을 초기화한다.
     *
     * @param key 로그에 사용되는 문자열 키
     */
    LoggerKey(String key) {
        this.key = key;
    }
}