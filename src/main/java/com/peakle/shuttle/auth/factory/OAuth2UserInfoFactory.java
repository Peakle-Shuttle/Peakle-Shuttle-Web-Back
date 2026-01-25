package com.peakle.shuttle.auth.factory;

import com.peakle.shuttle.auth.provider.AuthProvider;
import com.peakle.shuttle.util.oauth2.KakaoOAuth2UserInfo;
import com.peakle.shuttle.util.oauth2.OAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        switch (authProvider) {
            case KAKAO:
                return new KakaoOAuth2UserInfo(attributes);
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + authProvider);
        }
    }
}
