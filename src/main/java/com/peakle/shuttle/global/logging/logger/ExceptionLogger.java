package com.peakle.shuttle.global.logging.logger;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.peakle.shuttle.global.enums.logging.LoggerKey.*;
import static com.peakle.shuttle.global.enums.logging.LoggerMessage.EXCEPTION_CLIENT;
import static com.peakle.shuttle.global.enums.logging.LoggerMessage.EXCEPTION_SERVER;
import static net.logstash.logback.argument.StructuredArguments.keyValue;

/** 예외 발생 시 서버/클라이언트 에러를 구조화하여 로깅하는 컴포넌트 */
@Slf4j
@Component
public class ExceptionLogger {
    private static final Logger exceptionLogger = LoggerFactory.getLogger("exception");
    private static final int STACK_TRACE_LENGTH = 40;

    /**
     * 서버 측 예외 정보를 구조화된 형태로 에러 레벨 로그에 기록한다.
     *
     * 스택 트레이스는 최대 STACK_TRACE_LENGTH(기본 40) 행으로 잘라서 함께 기록하며,
     * 로그에 상태 코드(status), 예외 메시지(message), 잘린 스택 트레이스(stackTrace)를 포함한다.
     *
     * @param stackTraceElements 기록할 예외의 전체 스택 트레이스 배열
     * @param message            예외 메시지(로그에 기록할 설명 문자열)
     * @param status             HTTP 또는 처리 상태 코드 (로그에 함께 포함될 정수)
     */
    public void logServerError(StackTraceElement[] stackTraceElements, String message, int status) {
        int limit = Math.min(STACK_TRACE_LENGTH, stackTraceElements.length);
        StringBuilder stackTrace = new StringBuilder();

        for (int i = 0; i < limit; i++) {
            stackTrace.append(stackTraceElements[i].toString()).append(System.lineSeparator());
        }

        exceptionLogger.error(
                EXCEPTION_SERVER.getMessage(),
                keyValue(STATUS.getKey(), status),
                keyValue(ERROR_MESSAGE.getKey(), message),
                keyValue(STACK_TRACE.getKey(), stackTrace.toString())
        );
    }

    /**
     * 클라이언트 측 예외 정보를 구조화된 형태로 경고 레벨로 기록합니다.
     *
     * @param message 클라이언트 예외의 설명 메시지
     * @param status  관련 HTTP 상태 코드
     */
    public void logClientError(String message, int status) {
        exceptionLogger.warn(
                EXCEPTION_CLIENT.getMessage(),
                keyValue(STATUS.getKey(), status),
                keyValue(ERROR_MESSAGE.getKey(), message)
        );
    }
}