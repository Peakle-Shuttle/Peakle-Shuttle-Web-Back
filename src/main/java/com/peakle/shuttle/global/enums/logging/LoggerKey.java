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

    LoggerKey(String key) {
        this.key = key;
    }
}
