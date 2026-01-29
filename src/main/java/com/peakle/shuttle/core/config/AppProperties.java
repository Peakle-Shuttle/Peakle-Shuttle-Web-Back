package com.peakle.shuttle.core.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/** 애플리케이션 설정 프로퍼티 (CORS, OAuth2) */
@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Cors cors = new Cors();
    private OAuth2 oauth2 = new OAuth2();

    @Getter
    @Setter
    public static class Cors {
        @NotBlank (message = "app.cors.allowed-origins 값이 필요합니다.")
        private String allowedOrigins;
    }

    @Getter
    @Setter
    public static class OAuth2 {
        private String redirectUri;
    }
}
