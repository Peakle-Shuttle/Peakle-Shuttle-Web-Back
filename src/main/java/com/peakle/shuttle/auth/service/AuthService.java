package com.peakle.shuttle.auth.service;

import com.peakle.shuttle.auth.dto.JwtProperties;
import com.peakle.shuttle.auth.dto.request.*;
import com.peakle.shuttle.auth.dto.response.TokenResponse;
import com.peakle.shuttle.auth.entity.RefreshToken;
import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.factory.AuthProviderFactory;
import com.peakle.shuttle.auth.provider.JwtProvider;
import com.peakle.shuttle.auth.repository.RefreshTokenRepository;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.core.exception.extend.InvalidArgumentException;
import com.peakle.shuttle.global.enums.AuthProvider;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.global.enums.Role;
import com.peakle.shuttle.global.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.isNull;

/** 인증 관련 비즈니스 로직을 처리하는 서비스 (회원가입, 로그인, 토큰 갱신, 로그아웃) */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class    AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final AuthProviderFactory authProviderFactory;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * LOCAL 회원가입을 처리하고 토큰을 발급합니다.
     *
     * @param request 회원가입 요청 정보
     * @return 발급된 Access/Refresh 토큰
     * @throws InvalidArgumentException 아이디 또는 이메일이 중복된 경우
     */
    @Transactional
    public TokenResponse signupLocal(SignupRequest request) {
        if (userRepository.existsByUserIdAndStatus(request.getUserId(), Status.ACTIVE)) {
            throw new InvalidArgumentException(ExceptionCode.DUPLICATE_ID);
        }

        if (request.getUserEmail() != null && userRepository.existsByUserEmailAndStatus(request.getUserEmail(), Status.ACTIVE)) {
            throw new InvalidArgumentException(ExceptionCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .userId(request.getUserId())
                .userPassword(passwordEncoder.encode(request.getUserPassword()))
                .userEmail(request.getUserEmail())
                .userName(request.getUserName())
                .userGender(request.getUserGender())
                .userNumber(request.getUserNumber())
                .userBirth(request.getUserBirth())
                .userSchool(request.getUserSchool())
                .userMajor(request.getUserMajor())
                .userRole(Role.ROLE_USER)
                .provider(AuthProvider.LOCAL)
                .build();

        User signInUser = userRepository.save(user);

        TokenResponse tokenResponse = jwtProvider.createTokenResponse(new AuthUserRequest(signInUser.getUserCode(), signInUser.getUserRole()));
        saveRefreshToken(signInUser.getUserCode(), tokenResponse.refreshToken());
        return tokenResponse;
    }

    /**
     * 카카오 회원가입을 처리하고 토큰을 발급합니다.
     *
     * @param request 카카오 회원가입 요청 정보
     * @return 발급된 Access/Refresh 토큰
     * @throws AuthException provider ID가 null인 경우
     * @throws InvalidArgumentException 아이디 또는 이메일이 중복된 경우
     */
    @Transactional
    public TokenResponse signupKakao(KakaoSignupRequest request) {
        String providerId = authProviderFactory.getAuthProviderId(AuthProvider.KAKAO, request.getProviderToken());

        if (isNull(providerId)) {
            throw new AuthException(ExceptionCode.ANOTHER_PROVIDER);
        }

        String userId = "kakao_" + providerId;

        if (userRepository.existsByUserIdAndStatus(userId, Status.ACTIVE)) {
            throw new InvalidArgumentException(ExceptionCode.DUPLICATE_ID);
        }

        if (request.getUserEmail() != null && userRepository.existsByUserEmailAndStatus(request.getUserEmail(), Status.ACTIVE)) {
            throw new InvalidArgumentException(ExceptionCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .userId(userId)
                .userPassword(passwordEncoder.encode(UUID.randomUUID().toString()))
                .userEmail(request.getUserEmail())
                .userName(request.getUserName())
                .userGender(request.getUserGender())
                .userNumber(request.getUserNumber())
                .userBirth(request.getUserBirth())
                .userSchool(request.getUserSchool())
                .userMajor(request.getUserMajor())
                .userRole(Role.ROLE_USER)
                .provider(AuthProvider.KAKAO)
                .providerId(providerId)
                .build();

        User signInUser = userRepository.save(user);

        TokenResponse tokenResponse = jwtProvider.createTokenResponse(new AuthUserRequest(signInUser.getUserCode(), signInUser.getUserRole()));
        saveRefreshToken(signInUser.getUserCode(), tokenResponse.refreshToken());
        return tokenResponse;
    }

    /**
     * 아이디/비밀번호로 로그인을 처리합니다.
     *
     * @param request 로그인 요청 정보
     * @return 발급된 Access/Refresh 토큰
     * @throws InvalidArgumentException 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserId(), request.getUserPassword())
        );

        User user = userRepository.findByUserIdAndStatus(request.getUserId(), Status.ACTIVE)
                .orElseThrow(() -> new InvalidArgumentException(ExceptionCode.NOT_FOUND_USER));

        TokenResponse tokenResponse = jwtProvider.createTokenResponse(new AuthUserRequest(user.getUserCode(), user.getUserRole()));
        saveRefreshToken(user.getUserCode(), tokenResponse.refreshToken());
        return tokenResponse;
    }

    /**
     * OAuth 간편 로그인을 처리합니다.
     *
     * @param request OAuth 로그인 요청 (provider 정보 포함)
     * @return 발급된 Access/Refresh 토큰
     * @throws AuthException provider ID가 null이거나 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public TokenResponse oAuthLogin(final OAuthLoginRequest request) {
        //oAuthLogin 처리
        final String providerId = authProviderFactory.getAuthProviderId(request);
        
        if (isNull(providerId)) {
            throw new AuthException(ExceptionCode.ANOTHER_PROVIDER);
        }

        final User user = userRepository.findByProviderAndProviderIdAndStatus(AuthProvider.KAKAO, providerId, Status.ACTIVE)
                                        .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        TokenResponse tokenResponse = jwtProvider.createTokenResponse(new AuthUserRequest(user.getUserCode(), user.getUserRole()));
        saveRefreshToken(user.getUserCode(), tokenResponse.refreshToken());
        return tokenResponse;
    }

    /**
     * Refresh Token을 검증하고 새로운 토큰을 발급합니다.
     * 만료된 토큰은 삭제 후 예외를 발생시킵니다.
     *
     * @param refreshToken 갱신 요청에 사용할 Refresh Token
     * @return 재발급된 Access/Refresh 토큰
     * @throws InvalidArgumentException 토큰이 유효하지 않거나 만료된 경우
     */
    @Transactional
    public TokenResponse refresh(String refreshToken) {
        if  (jwtProvider.expired(refreshToken)) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(refreshTokenRepository::delete);
            throw new InvalidArgumentException(ExceptionCode.EXPIRED_REFRESH_TOKEN);
        }

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new InvalidArgumentException(ExceptionCode.EMPTY_REFRESH);
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidArgumentException(ExceptionCode.EMPTY_REFRESH));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new InvalidArgumentException(ExceptionCode.EXPIRED_REFRESH_TOKEN);
        }

        User user = userRepository.findByUserCodeAndStatus(storedToken.getUserCode(), Status.ACTIVE)
                .orElseThrow(() -> new InvalidArgumentException(ExceptionCode.NOT_FOUND_USER));

        refreshTokenRepository.delete(storedToken);

        TokenResponse tokenResponse = jwtProvider.recreateTokenResponse(new AuthUserRequest(user.getUserCode(), user.getUserRole()), refreshToken);
        saveRefreshToken(user.getUserCode(), tokenResponse.refreshToken());
        return tokenResponse;
    }

    /**
     * 로그아웃을 처리하고 저장된 Refresh Token을 삭제합니다.
     *
     * @param refreshToken 삭제할 Refresh Token
     */
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    private void saveRefreshToken(Long userCode, String refreshToken) {
        LocalDateTime expiryDate = LocalDateTime.now()
                .plusSeconds(jwtProperties.getRefreshTokenValidity() / 1000);

        refreshTokenRepository.findByUserCode(userCode)
                .ifPresentOrElse(
                        existing -> existing.updateToken(refreshToken, expiryDate),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .token(refreshToken)
                                .userCode(userCode)
                                .expiryDate(expiryDate)
                                .build())
                );
    }
}
