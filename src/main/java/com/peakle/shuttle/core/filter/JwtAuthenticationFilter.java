package com.peakle.shuttle.core.filter;

/*
  JWT 인증 필터

  TODO: 보완점
  1. [권장] Optional 사용 - null 대신 Optional<String> 반환
  2. [권장] LoggingContextManager 추가 - 사용자 추적 로깅
  3. [선택] OAuthUserDetails 연동 - 인증 후 사용자 정보 컨텍스트 설정
 */

import com.peakle.shuttle.auth.provider.JwtProvider;
import com.peakle.shuttle.core.exception.extend.JwtException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** 요청 헤더에서 JWT 토큰을 추출하여 인증 정보를 SecurityContext에 설정하는 필터 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final String AUTHORIZATION_HEADER;
    private final String GRANT_TYPE;
    /**
     * 요청 처리 시 사용할 JWT 제공자와 헤더/접두사 설정을 주입하여 JwtAuthenticationFilter를 초기화한다.
     *
     * @param jwtProvider  토큰의 검증과 Authentication 객체 생성을 담당하는 JwtProvider
     * @param accessHeader JWT를 담는 요청 헤더의 이름(예: "Authorization")
     * @param grantType    헤더 값 앞의 토큰 접두사(예: "Bearer")
     */

    public JwtAuthenticationFilter(
        JwtProvider jwtProvider,
        @Value("${jwt.access-header}") String accessHeader,
        @Value("${jwt.grant-type}") String grantType
        // ,LoggingContextManager loggingContextManager
    ) {
        this.jwtProvider = jwtProvider;
        this.AUTHORIZATION_HEADER = accessHeader;
        this.GRANT_TYPE = grantType;
//        this.logginfConextManager = loggingContextManager
    }

    /**
     * 요청마다 HTTP 요청 헤더에서 JWT를 추출·검증하고, 토큰이 유효하면 해당 인증 정보를 SecurityContext에 저장합니다.
     *
     * 요청 처리는 이후의 필터 체인으로 계속 진행됩니다.
     *
     * @throws ServletException 요청 처리 중 서블릿 관련 오류가 발생한 경우
     * @throws IOException 요청 또는 응답의 입출력 처리 중 오류가 발생한 경우
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청의 Authorization 헤더에서 JWT 토큰 문자열을 추출한다.
     *
     * @param request HTTP 요청
     * @return 토큰 문자열. Authorization 헤더가 없거나 비어있으면 {@code null}.
     * @throws JwtException 헤더가 설정된 인증 타입 접두사(GRANT_TYPE + " ")로 시작하지 않는 경우 {@code ExceptionCode.NOT_EXIST_BEARER_SUFFIX}를 담은 예외를 던진다.
     */
    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authHeader)) {
            return null;
        }

        String suffix = GRANT_TYPE + " ";
        if (!authHeader.startsWith(suffix)) {
            throw new JwtException(ExceptionCode.NOT_EXIST_BEARER_SUFFIX);
        }
        return authHeader.substring(suffix.length());
    }
}