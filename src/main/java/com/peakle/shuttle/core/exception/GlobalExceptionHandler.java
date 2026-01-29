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
//    }

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
//    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionResponse<Object>> handleValidationException(HandlerMethodValidationException e) {
        exceptionLogger.logClientError(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value());
        loggingContextManager.clear();

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ExceptionResponse<>(
                                HttpStatus.UNPROCESSABLE_ENTITY.toString(),
                                e.getMessage(),
                                null
                        )
                );
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ExceptionResponse<Object>> handleIOException(IOException e) {
        exceptionLogger.logServerError(e.getStackTrace(), e.getMessage(), HttpStatus.BAD_REQUEST.value());
        loggingContextManager.clear();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse<>(
                                HttpStatus.BAD_REQUEST.toString(),
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
//    }

    private String getStackTraceConvertString(Exception e) {
        StringBuilder sb = new StringBuilder();

        for (StackTraceElement ste : e.getStackTrace()) {
            sb.append(ste.toString()).append("\n");
        }

        return sb.toString();
    }
}