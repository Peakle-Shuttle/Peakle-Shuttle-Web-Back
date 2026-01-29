package com.peakle.shuttle.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** BCrypt 기반 PasswordEncoder Bean 설정 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Spring에서 주입 가능한 BCrypt 기반 PasswordEncoder 빈을 제공한다.
     *
     * @return BCryptPasswordEncoder 인스턴스(PasswordEncoder 구현)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}