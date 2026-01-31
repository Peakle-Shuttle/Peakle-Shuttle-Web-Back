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

    private String toJson(Object data) throws JsonProcessingException {
        return mapper.writeValueAsString(data);
    }
}