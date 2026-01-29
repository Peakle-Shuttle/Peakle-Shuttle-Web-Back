package com.peakle.shuttle.core.config;

import com.peakle.shuttle.core.filter.JwtAuthenticationFilter;
import com.peakle.shuttle.core.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/** Spring Security 설정 (JWT 필터 체인, CORS, 경로별 인가 규칙) */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * 애플리케이션의 보안 필터 체인을 구성하고 반환합니다.
     *
     * CORS 설정을 적용하고 CSRF, 폼 로그인, HTTP 기본 인증을 비활성화하며 세션 생성 정책을 STATELESS로 설정하고,
     * 경로별 인가 규칙을 적용한 뒤 JWT 인증 필터와 JWT 예외 처리 필터를 등록합니다.
     *
     * @param http 보안 구성을 위한 HttpSecurity 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 구성 또는 빌드 과정에서 오류가 발생한 경우
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(setAuthorizePath())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 인증 및 권한 부여 규칙을 구성하여 특정 공개 엔드포인트는 허용하고 나머지 요청은 인증을 요구하도록 설정한다.
     *
     * 구성된 공개 엔드포인트:
     * - 인증 및 OAuth 관련: /auth/**, /oauth2/**, /login/oauth2/**
     * - 사용자 정보 조회(아이디/이메일/비밀번호): /user/info/id, /user/info/email, /user/info/pw
     * - 모니터링 및 오류: /actuator/health, /error
     * - Swagger 및 API 문서: /swagger-ui.html, /swagger-ui/**, /v3/api-docs/**
     *
     * @return 위의 경로들에 대해 공개 접근을 허용하고, 그 외 모든 요청에 대해 인증을 요구하는 `Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>`
     */
    @Bean
    public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> setAuthorizePath() {
        return auth -> auth
                // Login Request
                .requestMatchers("/auth/**", "/oauth2/**", "/login/oauth2/**").permitAll()
                // User Request (회원정보 조회 제외)
                .requestMatchers("/user/info/id", "/user/info/email", "/user/info/pw").permitAll()
                // Monitoring Request
                .requestMatchers("/actuator/health", "/error").permitAll()
                // Swagger Request
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated();
    }

    /**
     * AuthenticationConfiguration에서 AuthenticationManager를 조회하여 빈으로 등록한다.
     *
     * @param config AuthenticationManager를 제공하는 AuthenticationConfiguration
     * @return 애플리케이션에서 사용할 AuthenticationManager 인스턴스
     * @throws Exception AuthenticationManager를 조회할 수 없을 때 발생
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}