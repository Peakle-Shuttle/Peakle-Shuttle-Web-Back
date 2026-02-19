package com.peakle.shuttle.core.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.peakle.shuttle.core.exception.extend.JwtException;
import com.peakle.shuttle.core.exception.response.ExceptionResponse;
import com.peakle.shuttle.global.enums.ExceptionCode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** JWT 인증 과정에서 발생하는 예외를 처리하여 에러 응답을 반환하는 필터 */
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper mapper;

    public JwtExceptionFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * JWT 인증 과정에서 발생하는 JwtException을 처리합니다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 처리 중 예외 발생 시
     * @throws IOException 입출력 예외 발생 시
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException exception) {
            setErrorResponse(exception.getExceptionCode(), response);
        }
    }

    /**
     * 에러 응답을 JSON 형식으로 작성합니다.
     *
     * @param exceptionCode 예외 코드
     * @param response HTTP 응답
     */
    private void setErrorResponse(ExceptionCode exceptionCode, HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");

        try {
            response.getWriter().write(
                    toJson(
                            new ExceptionResponse<>(
                                    exceptionCode.getCode(),
                                    exceptionCode.getMessage(),
                                    null
                            )
                    )
            );
        } catch (IOException ignored) {

        }
    }

    /**
     * 객체를 JSON 문자열로 변환합니다.
     *
     * @param data 변환할 객체
     * @return JSON 문자열
     * @throws JsonProcessingException JSON 변환 실패 시
     */
    private String toJson(Object data) throws JsonProcessingException {
        return mapper.writeValueAsString(data);
    }
}