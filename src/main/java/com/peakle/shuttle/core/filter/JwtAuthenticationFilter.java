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

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final String AUTHORIZATION_HEADER;
    private final String GRANT_TYPE;
    // private final LoggingContextManager loggingContextManger;

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
