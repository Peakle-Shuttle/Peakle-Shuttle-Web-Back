package com.peakle.shuttle.auth.provider.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peakle.shuttle.auth.key.OidcPublicKeyList;
import com.peakle.shuttle.auth.provider.JwtProvider;
import com.peakle.shuttle.auth.provider.OidcProvider;
import com.peakle.shuttle.auth.provider.PublicKeyProvider;
import com.peakle.shuttle.global.client.KakaoAuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

/** 카카오 OIDC 인증을 처리하는 Provider 구현체 */
@Component
@RequiredArgsConstructor
public class KakaoAuthProvider implements OidcProvider {
    private final KakaoAuthClient kakaoAuthClient;
    private final JwtProvider jwtProvider;
    private final PublicKeyProvider publicKeyProvider;
    private final ObjectMapper objectMapper;

    /**
     * 카카오 ID Token을 검증하고 사용자 고유 ID(subject)를 반환합니다.
     *
     * @param idToken 카카오 OIDC ID Token
     * @return 카카오 사용자 고유 ID
     */
    @Override
    public String getProviderId(final String idToken) {
        final OidcPublicKeyList oidcPublicKeyList = kakaoAuthClient.getPublicKeys();
        final PublicKey publicKey = publicKeyProvider.generatePublicKey(parseHeaders(idToken, objectMapper), oidcPublicKeyList);

        return jwtProvider.parseClaims(idToken, publicKey).getSubject();
    }

}
