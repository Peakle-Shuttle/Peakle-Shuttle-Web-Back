package com.peakle.shuttle.auth.controller;

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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원가입, 로그인/로그아웃, 카카오톡 간편 로그인 API")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary =  "회원가입",
            description = "회원가입 API 입니다."
    )
    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(@Valid @RequestBody SignupRequest request) {
        TokenResponse response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "로그인 API",
            description = "로그인을 진행합니다."
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "OAuth 간편 로그인 API",
            description = "로그인을 진행합니다."
    )
    @PostMapping("/login/oAuth")
    public ResponseEntity<TokenResponse> oAuthLogin(@Valid @RequestBody OAuthLoginRequest request) {
        TokenResponse response = authService.oAuthLogin(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "토큰 재 생성 API",
            description = "RefreshToken를 통해 재발급 요청하면 검증 후 새로 생성된 토큰을 반환합니다."
    )
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        TokenResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "로그아웃 API",
            description = "로그 아웃 API입니다. FCM TOKEN을 비활성화 합니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody TokenRefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
