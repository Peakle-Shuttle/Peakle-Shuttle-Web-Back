package com.peakle.shuttle.core.exception.extend;

import com.peakle.shuttle.global.enums.ExceptionCode;
import lombok.Getter;

/** JWT 토큰 관련 커스텀 예외 */
@Getter
public class JwtException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    /**
     * JWT 관련 예외를 ExceptionCode와 함께 생성한다.
     *
     * @param exceptionCode 예외를 식별하는 코드; 해당 코드의 메시지가 예외 메시지로 설정된다.
     */
    public JwtException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}