package com.peakle.shuttle.core.exception.extend;

import com.peakle.shuttle.global.enums.ExceptionCode;
import lombok.Getter;

/** 인증/인가 관련 커스텀 예외 */
@Getter
public class AuthException extends RuntimeException{
    private final ExceptionCode exceptionCode;

    /**
     * 주어진 ExceptionCode로 AuthException을 생성하고 예외 메시지를 해당 코드의 메시지로 초기화합니다.
     *
     * @param exceptionCode 예외를 설명하는 코드 및 메시지; 이 코드의 `getMessage()` 값이 예외 메시지로 설정되며 내부에 저장됩니다.
     */
    public AuthException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}