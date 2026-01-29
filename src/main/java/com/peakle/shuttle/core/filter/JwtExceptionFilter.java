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

    /**
     * JwtExceptionFilter를 초기화하고 JSON 직렬화를 위한 ObjectMapper를 설정한다.
     *
     * @param mapper 에러 응답을 JSON으로 직렬화하는 데 사용할 Jackson ObjectMapper
     */
    public JwtExceptionFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * JWT 관련 예외가 발생하면 표준화된 JSON 오류 응답을 작성하고, 그렇지 않으면 요청을 다음 필터로 전달합니다.
     *
     * JwtException을 잡아 응답에 HTTP 401 상태와 JSON 오류 본문을 설정하며, 다른 예외는 필터 체인을 통해 전파됩니다.
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
     * 주어진 예외 코드로 HTTP 응답을 401 상태의 JSON 오류 응답으로 작성한다.
     *
     * 응답 본문은 `ExceptionResponse` 형태의 JSON으로 직렬화되어 `code`, `message`, `data`(null)를 포함하며,
     * Content-Type은 `application/json; charset=UTF-8`로 설정된다. 응답 쓰기 중 발생한 IO 오류는 무시된다.
     *
     * @param exceptionCode 응답에 포함할 예외 코드와 메시지를 제공하는 객체
     * @param response      HTTP 응답을 구성하고 작성할 HttpServletResponse
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
     * 객체를 JSON 문자열로 직렬화한다.
     *
     * @param data 직렬화할 객체
     * @return 지정된 객체의 JSON 표현 문자열
     * @throws JsonProcessingException 직렬화 중 오류가 발생한 경우
     */
    private String toJson(Object data) throws JsonProcessingException {
        return mapper.writeValueAsString(data);
    }
}