package com.peakle.shuttle.global.util.oauth2;

import com.peakle.shuttle.auth.service.AuthService;
import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.domain.UserRepository;
import com.peakle.shuttle.auth.dto.response.TokenResponse;
import com.peakle.shuttle.core.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    public OAuth2AuthenticationSuccessHandler(@Lazy AuthService authService,
                                               UserRepository userRepository,
                                               AppProperties appProperties) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.appProperties = appProperties;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        User user = userRepository.findById(oAuth2User.getUserCode())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        TokenResponse tokenResponse = authService.createTokenResponse(user);

        String redirectUri = UriComponentsBuilder.fromUriString(appProperties.getOauth2().getRedirectUri())
                .queryParam("accessToken", tokenResponse.getAccessToken())
                .queryParam("refreshToken", tokenResponse.getRefreshToken())
                .build().toUriString();

        log.info("OAuth2 로그인 성공. 리다이렉트: {}", appProperties.getOauth2().getRedirectUri());

        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}
