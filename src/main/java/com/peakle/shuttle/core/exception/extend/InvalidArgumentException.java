package com.peakle.shuttle.core.exception.extend;

import com.peakle.shuttle.global.enums.ExceptionCode;
import lombok.Getter;

/** ExceptionCode 기반의 커스텀 IllegalArgumentException */
@Getter
public class InvalidArgumentException extends java.lang.IllegalArgumentException {
    private final ExceptionCode exceptionCode;

    /**
     * 주어진 ExceptionCode로 InvalidArgumentException 인스턴스를 생성하고 예외 메시지를 해당 ExceptionCode의 메시지로 설정한다.
     *
     * @param exceptionCode 예외 메시지와 코드 정보를 제공하는 {@link com.peakle.shuttle.global.enums.ExceptionCode}
     */
    public InvalidArgumentException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}