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

    /**
     * KakaoAuthProvider를 사용해 AuthProviderFactory를 생성하고 내부 제공자 매핑을 초기화한다.
     *
     * @param kakaoAuthProvider 주입된 카카오 OIDC 공급자 구현체
     */
    public AuthProviderFactory(KakaoAuthProvider kakaoAuthProvider) {
        this.authTypeMap = new EnumMap<>(AuthProvider.class);
        this.kakaoAuthProvider = kakaoAuthProvider;

        init();
    }

    /**
     * OAuth 공급자 매핑을 초기화하여 AuthProvider.KAKAO를 KakaoAuthProvider에 등록한다.
     */
    private void init() {
        authTypeMap.put(AuthProvider.KAKAO, kakaoAuthProvider);
    }

    /**
     * OAuth 로그인 요청에서 해당 OAuth 제공자가 식별하는 사용자 고유 ID를 조회합니다.
     *
     * @param request OAuth 로그인 요청
     * @return 해당 제공자에서 식별하는 사용자 고유 ID
     * @throws JwtException 제공자가 지원되지 않는 경우
     */
    public String getAuthProviderId(OAuthLoginRequest request) {
        return getProvider(request.authProvider()).getProviderId(request.providerToken());
    }

    /**
     * 주어진 AuthProvider에 대응하는 OidcProvider 구현체를 반환한다.
     *
     * @param provider OIDC 공급자를 식별하는 AuthProvider 열거값
     * @return 해당 AuthProvider에 매핑된 OidcProvider 인스턴스
     * @throws JwtException 공급자에 대한 매핑이 없을 경우 ExceptionCode.INVALID_PROVIDER로 발생
     */
    private OidcProvider getProvider(AuthProvider provider) {
        OidcProvider oidcProvider = authTypeMap.get(provider);

        if (oidcProvider == null) {
            throw new JwtException(ExceptionCode.INVALID_PROVIDER);
        }

        return oidcProvider;
    }
}