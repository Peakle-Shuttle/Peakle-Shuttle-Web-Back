package com.peakle.shuttle.core.exception.extend;

import com.peakle.shuttle.global.enums.ExceptionCode;
import lombok.Getter;

/** ExceptionCode 기반의 커스텀 IllegalArgumentException */
@Getter
public class InvalidArgumentException extends java.lang.IllegalArgumentException {
    private final ExceptionCode exceptionCode;

    public InvalidArgumentException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}