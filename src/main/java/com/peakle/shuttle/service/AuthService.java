package com.peakle.shuttle.service;

import com.peakle.shuttle.dto.LoginRequest;
import com.peakle.shuttle.dto.SignupRequest;
import com.peakle.shuttle.dto.TokenResponse;
import com.peakle.shuttle.entity.*;
import com.peakle.shuttle.repository.RefreshTokenRepository;
import com.peakle.shuttle.repository.UserRepository;
import com.peakle.shuttle.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Long signup(SignupRequest request) {
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (request.getUserEmail() != null && userRepository.existsByUserEmail(request.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
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

        return userRepository.save(user).getUserCode();
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserId(), request.getUserPassword())
        );

        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return createTokenResponse(user);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("리프레시 토큰을 찾을 수 없습니다."));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다.");
        }

        User user = userRepository.findById(storedToken.getUserCode())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        refreshTokenRepository.delete(storedToken);

        return createTokenResponse(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public TokenResponse createTokenResponse(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getUserCode(),
                user.getUserId(),
                user.getUserRole().getKey()
        );

        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getUserCode());

        refreshTokenRepository.findByUserCode(user.getUserCode())
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .userCode(user.getUserCode())
                .expiryDate(LocalDateTime.now().plusSeconds(
                        jwtTokenProvider.getRefreshTokenValidity() / 1000))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return TokenResponse.of(
                accessToken,
                newRefreshToken,
                jwtTokenProvider.getRefreshTokenValidity() / 1000
        );
    }
}
