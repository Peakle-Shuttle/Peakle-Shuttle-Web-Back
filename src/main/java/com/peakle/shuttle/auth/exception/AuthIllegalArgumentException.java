package com.peakle.shuttle.auth.exception;

import com.peakle.shuttle.global.enums.ExceptionCode;
import lombok.Getter;

@Getter
public class AuthIllegalArgumentException extends IllegalArgumentException {
    private final ExceptionCode exceptionCode;

    public AuthIllegalArgumentException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}