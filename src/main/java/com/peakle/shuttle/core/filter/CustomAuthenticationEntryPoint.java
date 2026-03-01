package com.peakle.shuttle.core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peakle.shuttle.core.exception.response.ExceptionResponse;
import com.peakle.shuttle.global.enums.ExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** 인증되지 않은 요청(Authorization 헤더 없음)에 대해 ExceptionResponse 형식으로 401 응답을 반환하는 EntryPoint */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    /**
     * 인증되지 않은 요청에 대해 401 Unauthorized 응답을 ExceptionResponse 형식으로 반환합니다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param authException 인증 실패 예외
     * @throws IOException 응답 작성 중 입출력 예외 발생 시
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ExceptionCode exceptionCode = ExceptionCode.EMPTY_AUTORIZATION;

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(
                mapper.writeValueAsString(
                        new ExceptionResponse<>(
                                exceptionCode.getCode(),
                                exceptionCode.getMessage(),
                                null
                        )
                )
        );
    }
}
