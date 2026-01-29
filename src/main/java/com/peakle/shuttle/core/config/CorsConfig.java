package com.peakle.shuttle.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/** CORS 설정 (허용 Origin, 메서드, 헤더 정의) */
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final AppProperties appProperties;

    /**
     * 애플리케이션 설정에 정의된 CORS 규칙으로 구성된 CorsConfigurationSource 빈을 생성한다.
     *
     * 구성에는 설정된 허용 출처(앱 프로퍼티에서 파싱), HTTP 메서드 목록, 허용 헤더, 노출 헤더,
     * 자격 증명 허용 및 프리플라이트 캐시 최대 연령이 포함되며 전체 경로("/**")에 등록된다.
     *
     * @return 구성된 CorsConfigurationSource 인스턴스 (전체 경로에 적용된 CORS 구성 포함)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        String[] origins = appProperties.getCors().getAllowedOrigins().split(",");
        configuration.setAllowedOrigins(Arrays.asList(origins));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}