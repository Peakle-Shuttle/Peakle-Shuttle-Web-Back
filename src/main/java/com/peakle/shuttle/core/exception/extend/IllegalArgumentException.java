package com.peakle.shuttle.core.exception.extend;

import com.peakle.shuttle.global.enums.ExceptionCode;
import lombok.Getter;

@Getter
public class IllegalArgumentException extends java.lang.IllegalArgumentException {
    private final ExceptionCode exceptionCode;

    public IllegalArgumentException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}