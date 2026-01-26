package com.peakle.shuttle.auth.service;

import com.peakle.shuttle.auth.domain.*;
import com.peakle.shuttle.auth.dto.Role;
import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.provider.AuthProvider;
import com.peakle.shuttle.global.util.oauth2.CustomOAuth2User;
import com.peakle.shuttle.global.util.oauth2.OAuth2UserInfo;
import com.peakle.shuttle.auth.factory.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        User user = userRepository.findByProviderAndProviderId(provider, userInfo.getId())
                .orElseGet(() -> registerNewUser(provider, userInfo));

        return new CustomOAuth2User(
                user.getUserCode(),
                user.getUserId(),
                user.getUserRole(),
                oAuth2User.getAttributes()
        );
    }

    private User registerNewUser(AuthProvider provider, OAuth2UserInfo userInfo) {
        String uniqueUserId = provider.name().toLowerCase() + "_" + userInfo.getId();

        if (userRepository.existsByUserId(uniqueUserId)) {
            uniqueUserId = uniqueUserId + "_" + UUID.randomUUID().toString().substring(0, 8);
        }

        User user = User.builder()
                .userId(uniqueUserId)
                .userEmail(userInfo.getEmail())
                .userName(userInfo.getNickname())
                .userRole(Role.ROLE_USER)
                .provider(provider)
                .providerId(userInfo.getId())
                .build();

        return userRepository.save(user);
    }
}
