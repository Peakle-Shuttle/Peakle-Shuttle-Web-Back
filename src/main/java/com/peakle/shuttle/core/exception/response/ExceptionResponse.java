package com.peakle.shuttle.core.exception.response;

/** 예외 응답 DTO (에러 코드, 메시지, 추가 데이터) */
public record ExceptionResponse<T>(String code, String message, T data) {
}
