package com.peakle.shuttle.auth.controller;

import com.peakle.shuttle.auth.dto.request.KakaoSignupRequest;
import com.peakle.shuttle.auth.dto.request.LoginRequest;
import com.peakle.shuttle.auth.dto.request.OAuthLoginRequest;
import com.peakle.shuttle.auth.dto.request.SignupRequest;
import com.peakle.shuttle.auth.dto.request.TokenRefreshRequest;
import com.peakle.shuttle.auth.dto.response.TokenResponse;

import com.peakle.shuttle.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 인증 관련 API 컨트롤러 (회원가입, 로그인/로그아웃, OAuth 간편 로그인) */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원가입, 로그인/로그아웃, 카카오톡 간편 로그인 API")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary =  "회원가입",
            description = "LOCAL 회원가입 API 입니다."
    )
    /**
     * LOCAL 회원가입을 진행하고 토큰을 발급합니다.
     *
     * @param request 회원가입 요청 정보
     * @return 발급된 Access/Refresh 토큰
     */
    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(@Valid @RequestBody SignupRequest request) {
        TokenResponse response = authService.signupLocal(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "카카오 회원가입",
            description = "카카오 간편 회원가입 API 입니다."
    )
    /**
     * 카카오 회원가입을 진행하고 토큰을 발급합니다.
     *
     * @param request 카카오 회원가입 요청 정보 (providerToken + 프로필)
     * @return 발급된 Access/Refresh 토큰
     */
    @PostMapping("/signup/kakao")
    public ResponseEntity<TokenResponse> signupKakao(@Valid @RequestBody KakaoSignupRequest request) {
        TokenResponse response = authService.signupKakao(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "로그인 API",
            description = "로그인을 진행합니다."
    )
    /**
     * 아이디/비밀번호로 로그인합니다.
     *
     * @param request 로그인 요청 정보 (userId, userPassword)
     * @return 발급된 Access/Refresh 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "OAuth 간편 로그인 API",
            description = "로그인을 진행합니다."
    )
    /**
     * OAuth 간편 로그인을 진행합니다.
     *
     * @param request OAuth 로그인 요청 (authProvider, providerToken)
     * @return 발급된 Access/Refresh 토큰
     */
    @PostMapping("/login/oAuth")
    public ResponseEntity<TokenResponse> oAuthLogin(@Valid @RequestBody OAuthLoginRequest request) {
        TokenResponse response = authService.oAuthLogin(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "토큰 재 생성 API",
            description = "RefreshToken를 통해 재발급 요청하면 검증 후 새로 생성된 토큰을 반환합니다."
    )
    /**
     * Refresh Token을 검증하고 새로운 토큰을 발급합니다.
     *
     * @param request Refresh Token 정보
     * @return 재발급된 Access/Refresh 토큰
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        TokenResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "로그아웃 API",
            description = "로그 아웃 API입니다. FCM TOKEN을 비활성화 합니다."
    )
    /**
     * 로그아웃을 진행하고 Refresh Token을 무효화합니다.
     *
     * @param request Refresh Token 정보
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody TokenRefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
