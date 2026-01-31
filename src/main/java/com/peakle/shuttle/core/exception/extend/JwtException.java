package com.peakle.shuttle.core.exception.extend;

import com.peakle.shuttle.global.enums.ExceptionCode;
import lombok.Getter;

/** JWT 토큰 관련 커스텀 예외 */
@Getter
public class JwtException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    public JwtException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
