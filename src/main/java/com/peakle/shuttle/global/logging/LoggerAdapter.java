package com.peakle.shuttle.global.logging;

/** 로깅 어댑터 인터페이스 (완료/에러 로깅) */
public interface LoggerAdapter {
    void logComplete(long executionTime);
    void logError(String message);
}