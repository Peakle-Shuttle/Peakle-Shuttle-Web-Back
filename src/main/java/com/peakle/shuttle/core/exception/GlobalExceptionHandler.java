package com.peakle.shuttle.core.exception;

import com.peakle.shuttle.core.exception.extend.*;
import com.peakle.shuttle.core.exception.response.ExceptionResponse;

import com.peakle.shuttle.global.logging.LoggingContextManager;
import com.peakle.shuttle.global.logging.logger.ExceptionLogger;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;


/** 전역 예외 처리 핸들러 (JWT, Auth, Validation, IO 예외 등) */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ExceptionLogger exceptionLogger;
    private final LoggingContextManager loggingContextManager;

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ExceptionResponse<Object>> handleEntityNotFoundException(ResourceNotFoundException e) {
//        exceptionLogger.logClientError(e.getMessage(), e.getExceptionCode().returnCode());
//        loggingContextManager.clear();
//
//        return ResponseEntity
//                .status(e.getExceptionCode().returnCode())
//                .body(new ExceptionResponse<>(
//                                e.getExceptionCode().getCode(),
//                                e.getExceptionCode().getMessage(),
//                                null
//                        )
//                );
/**
     * JWT 관련 예외를 처리하여 클라이언트에 적절한 HTTP 응답을 반환한다.
     *
     * 예외 정보를 서버 오류 로그로 남기고 로깅 컨텍스트를 초기화한 뒤,
     * 예외에 정의된 코드와 메시지를 담은 ExceptionResponse를 지정된 HTTP 상태로 반환한다.
     *
     * @param e 처리할 JwtException; 반환할 HTTP 상태와 응답 코드/메시지를 제공한다
     * @return 예외의 returnCode()를 HTTP 상태로 사용하고 예외 코드와 메시지를 본문으로 포함하는 ResponseEntity<ExceptionResponse<Object>>
     */

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionResponse<Object>> handleJwtExceptionException(JwtException e) {
        exceptionLogger.logServerError(e.getStackTrace(), e.getMessage(), e.getExceptionCode().returnCode());
        loggingContextManager.clear();

        return ResponseEntity
                .status(e.getExceptionCode().returnCode())
                .body(new ExceptionResponse<>(
                                e.getExceptionCode().getCode(),
                                e.getExceptionCode().getMessage(),
                                null
                        )
                );
    }

    /**
     * 인증 관련 예외를 처리하여 클라이언트에 표준화된 예외 응답과 해당 예외가 지정한 HTTP 상태 코드를 반환한다.
     *
     * @param e 처리할 AuthException. 응답의 HTTP 상태 코드와 본문 내용은 {@code e.getExceptionCode()}에서 가져온 값으로 구성된다.
     * @return 예외 코드와 메시지를 담은 {@code ExceptionResponse<Object>}를 본문으로 하고, 예외에서 결정한 HTTP 상태 코드를 사용하는 {@code ResponseEntity}.
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponse<Object>> handleAuthExceptionException(AuthException e) {
        exceptionLogger.logServerError(e.getStackTrace(), e.getMessage(), e.getExceptionCode().returnCode());
        loggingContextManager.clear();

        return ResponseEntity
                .status(e.getExceptionCode().returnCode())
                .body(new ExceptionResponse<>(
                                e.getExceptionCode().getCode(),
                                e.getExceptionCode().getMessage(),
                                null
                        )
                );
    }

//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<ExceptionResponse<Object>> handleBadRequestException(BadRequestException e) {
//        exceptionLogger.logClientError(e.getMessage(), e.getExceptionCode().returnCode());
//        loggingContextManager.clear();
//
//        return ResponseEntity
//                .status(e.getExceptionCode().returnCode())
//                .body(new ExceptionResponse<>(
//                                e.getExceptionCode().getCode(),
//                                e.getExceptionCode().getMessage(),
//                                null
//                        )
//                );
//    }

//    @ExceptionHandler(FileHandleException.class)
//    public ResponseEntity<ExceptionResponse<Object>> handleFileException(FileHandleException e) {
//        exceptionLogger.logServerError(e.getStackTrace(), e.getMessage(), e.getExceptionCode().returnCode());
//        loggingContextManager.clear();
//
//        return ResponseEntity
//                .status(e.getExceptionCode().returnCode())
//                .body(new ExceptionResponse<>(
//                                e.getExceptionCode().getCode(),
//                                e.getExceptionCode().getMessage(),
//                                null
//                        )
//                );
/**
     * 요청 핸들러 검증 실패를 처리하고 클라이언트 오류 응답을 생성한다.
     *
     * @param e 발생한 HandlerMethodValidationException
     * @return HTTP 422 상태를 가진 ResponseEntity&lt;ExceptionResponse&lt;Object&gt;&gt;.
     *         응답 본문은 코드 "422", 예외 메시지, 및 null 데이터를 포함한다.
     */

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionResponse<Object>> handleValidationException(HandlerMethodValidationException e) {
        exceptionLogger.logClientError(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value());
        loggingContextManager.clear();

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ExceptionResponse<>(
                                String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                                e.getMessage(),
                                null
                        )
                );
    }

    /**
     * 입출력 관련 예외가 발생했을 때 HTTP 400(Bad Request) 상태와 구조화된 예외 응답을 반환한다.
     *
     * 이 메서드는 예외 정보를 서버 오류로 기록하고 로깅 컨텍스트를 정리한 뒤 응답을 생성한다.
     *
     * @param e 발생한 IOException
     * @return HTTP 상태 400과 함께 body에 코드 `"400"`, 예외의 메시지, `data`가 `null`인 `ExceptionResponse`를 담은 `ResponseEntity`
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ExceptionResponse<Object>> handleIOException(IOException e) {
        exceptionLogger.logServerError(e.getStackTrace(), e.getMessage(), HttpStatus.BAD_REQUEST.value());
        loggingContextManager.clear();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse<>(
                                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                                e.getMessage(),
                                null
                        )
                );
    }

//    @ExceptionHandler(CursorException.class)
//    public ResponseEntity<ExceptionResponse<Object>> handleCursorException(CursorException e) {
//        exceptionLogger.logClientError(e.getMessage(), e.getExceptionCode().returnCode());
//        loggingContextManager.clear();
//
//        return ResponseEntity
//                .status(e.getExceptionCode().returnCode())
//                .body(new ExceptionResponse<>(
//                                e.getExceptionCode().getCode(),
//                                e.getExceptionCode().getMessage(),
//                                null
//                        )
//                );
//    }

//    @ExceptionHandler(PolicyException.class)
//    public ResponseEntity<ExceptionResponse<Object>> handlePolicyException(PolicyException e) {
//        exceptionLogger.logClientError(e.getMessage(), e.getExceptionCode().returnCode());
//        loggingContextManager.clear();
//
//        return ResponseEntity
//                .status(e.getExceptionCode().returnCode())
//                .body(new ExceptionResponse<>(
//                                e.getExceptionCode().getCode(),
//                                e.getExceptionCode().getMessage(),
//                                null
//                        )
//                );
//    }

//    @ExceptionHandler(AsyncException.class)
//    public ResponseEntity<ExceptionResponse<Object>> handleAsyncException(AsyncException e) {
//        exceptionLogger.logClientError(e.getMessage(), e.getExceptionCode().returnCode());
//        loggingContextManager.clear();
//
//        return ResponseEntity
//                .status(e.getExceptionCode().returnCode())
//                .body(new ExceptionResponse<>(
//                                e.getExceptionCode().getCode(),
//                                e.getExceptionCode().getMessage(),
//                                null
//                        )
//                );
//    }

//    @ExceptionHandler(ConcurrencyConflictException.class)
//    public ResponseEntity<ExceptionResponse<Object>> handleAsyncException(ConcurrencyConflictException e) {
//        exceptionLogger.logClientError(e.getMessage(), e.getExceptionCode().returnCode());
//        loggingContextManager.clear();
//
//        return ResponseEntity
//                .status(e.getExceptionCode().returnCode())
//                .eTag("\"" + e.getCurrentVersion() + "\"")
//                .body(new ExceptionResponse<>(
//                                e.getExceptionCode().getCode(),
//                                e.getExceptionCode().getMessage(),
//                                Map.of("currentVersion", e.getCurrentVersion())
//                        )
//                );
//    }

//        @ExceptionHandler(Exception.class)
//    public ResponseEntity<ExceptionResponse<Object>> handleOtherException(Exception e) {
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new ExceptionResponse<>(
//                                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
//                                String.format("%s with stack trace: %s", e.getMessage(), getStackTraceConvertString(e)),
//                                null
//                        )
//                );
/**
     * 예외의 스택 트레이스를 각 요소별로 줄바꿈하여 하나의 문자열로 생성한다.
     *
     * @return 스택 트레이스의 각 요소를 줄바꿈으로 구분한 문자열
     */

    private String getStackTraceConvertString(Exception e) {
        StringBuilder sb = new StringBuilder();

        for (StackTraceElement ste : e.getStackTrace()) {
            sb.append(ste.toString()).append("\n");
        }

        return sb.toString();
    }
}