package com.peakle.shuttle.core.exception;

import com.peakle.shuttle.global.enums.ExceptionCode;
import lombok.Getter;

@Getter
public class JwtException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    public JwtException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
