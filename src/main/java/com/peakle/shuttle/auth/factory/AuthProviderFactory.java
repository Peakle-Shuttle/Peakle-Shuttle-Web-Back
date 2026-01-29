package com.peakle.shuttle.auth.factory;

import com.peakle.shuttle.auth.dto.request.OAuthLoginRequest;
import com.peakle.shuttle.auth.provider.OidcProvider;
import com.peakle.shuttle.auth.provider.impl.KakaoAuthProvider;
import com.peakle.shuttle.core.exception.extend.JwtException;
import com.peakle.shuttle.global.enums.AuthProvider;
import com.peakle.shuttle.global.enums.ExceptionCode;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/** OAuth Provider를 관리하고 provider별 인증 ID를 조회하는 팩토리 */
@Component
public class AuthProviderFactory {
    private final Map<AuthProvider, OidcProvider> authTypeMap;
    private final KakaoAuthProvider kakaoAuthProvider;

    public AuthProviderFactory(KakaoAuthProvider kakaoAuthProvider) {
        this.authTypeMap = new EnumMap<>(AuthProvider.class);
        this.kakaoAuthProvider = kakaoAuthProvider;

        init();
    }

    private void init() {
        authTypeMap.put(AuthProvider.KAKAO, kakaoAuthProvider);
    }

    /**
     * OAuth 요청에서 provider ID를 조회합니다.
     *
     * @param request OAuth 로그인 요청
     * @return provider별 사용자 고유 ID
     * @throws JwtException 지원하지 않는 provider인 경우
     */
    public String getAuthProviderId(OAuthLoginRequest request) {
        return getProvider(request.authProvider()).getProviderId(request.providerToken());
    }

    private OidcProvider getProvider(AuthProvider provider) {
        OidcProvider oidcProvider = authTypeMap.get(provider);

        if (oidcProvider == null) {
            throw new JwtException(ExceptionCode.INVALID_PROVIDER);
        }

        return oidcProvider;
    }
}
