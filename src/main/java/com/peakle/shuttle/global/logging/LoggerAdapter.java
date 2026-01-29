package com.peakle.shuttle.global.logging;

/** 로깅 어댑터 인터페이스 (완료/에러 로깅) */
public interface LoggerAdapter {
    /**
 * 처리 완료를 기록하고 해당 실행 시간을 로깅한다.
 *
 * @param executionTime 처리에 소요된 총 경과 시간
 */
void logComplete(long executionTime);
    /**
 * 오류 이벤트를 기록한다.
 *
 * @param message 기록할 오류 메시지(오류 원인 또는 상세 설명)
void logError(String message);
}