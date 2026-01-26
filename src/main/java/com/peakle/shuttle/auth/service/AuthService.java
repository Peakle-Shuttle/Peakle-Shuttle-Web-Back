package com.peakle.shuttle.auth.service;

import com.peakle.shuttle.auth.domain.*;
import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.auth.entity.RefreshToken;
import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.exception.AuthIllegalArgumentException;
import com.peakle.shuttle.auth.provider.AuthProvider;
import com.peakle.shuttle.auth.provider.JwtProvider;
import com.peakle.shuttle.auth.dto.request.LoginRequest;
import com.peakle.shuttle.auth.dto.request.SignupRequest;
import com.peakle.shuttle.auth.dto.response.TokenResponse;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class    AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new AuthIllegalArgumentException(ExceptionCode.DUPLICATE_EMAIL);
        }

        if (request.getUserEmail() != null && userRepository.existsByUserEmail(request.getUserEmail())) {
            throw new AuthIllegalArgumentException(ExceptionCode.DUPLICATE_EMAIL);
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

        return jwtProvider.createTokenResponse(new AuthUserRequest(signInUser.getUserCode(), signInUser.getUserRole()));
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserId(), request.getUserPassword())
        );

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new AuthIllegalArgumentException(ExceptionCode.NOT_FOUND_USER));

        return jwtProvider.createTokenResponse(new AuthUserRequest(user.getUserCode(), user.getUserRole()));
    }

    @Transactional
    public TokenResponse oAuthLogin(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserId(), request.getUserPassword())
        );

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(ExceptionCode.NOT_FOUND_USER.getMessage()));

        return jwtProvider.createTokenResponse(new AuthUserRequest(user.getUserCode(), user.getUserRole()));
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new AuthIllegalArgumentException(ExceptionCode.EMPTY_REFRESH);
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthIllegalArgumentException(ExceptionCode.EMPTY_REFRESH));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new AuthIllegalArgumentException(ExceptionCode.EXPIRED_REFRESH_TOKEN);
        }

        User user = userRepository.findById(storedToken.getUserCode())
                .orElseThrow(() -> new AuthIllegalArgumentException(ExceptionCode.NOT_FOUND_USER));

        refreshTokenRepository.delete(storedToken);

        return jwtProvider.recreateTokenResponse(new AuthUserRequest(user.getUserCode(), user.getUserRole()), refreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
//    @Transactional
//    public TokenResponse createTokenResponse(User user) {
//        String accessToken = jwtTokenProvider.createAccessToken(
//                user.getUserCode(),
//                user.getUserId(),
//                user.getUserRole().getKey()
//        );
//
//        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserCode());
//
//        refreshTokenRepository.findByUserCode(user.getUserCode())
//                .ifPresent(refreshTokenRepository::delete);
//
//        RefreshToken refreshTokenEntity = RefreshToken.builder()
//                .token(newRefreshToken)
//                .userCode(user.getUserCode())
//                .expiryDate(LocalDateTime.now().plusSeconds(
//                        jwtTokenProvider.getRefreshTokenValidity() / 1000))
//                .build();
//
//        refreshTokenRepository.save(refreshTokenEntity);
//
//        return TokenResponse.of(
//                accessToken,
//                newRefreshToken,
//                jwtTokenProvider.getRefreshTokenValidity() / 1000
//        );
//    }
}
