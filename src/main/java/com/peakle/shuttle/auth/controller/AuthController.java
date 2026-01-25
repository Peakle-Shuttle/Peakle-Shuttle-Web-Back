package com.peakle.shuttle.auth.controller;

import com.peakle.shuttle.auth.dto.request.LoginRequest;
import com.peakle.shuttle.auth.dto.request.SignupRequest;
import com.peakle.shuttle.auth.dto.request.TokenRefreshRequest;
import com.peakle.shuttle.auth.dto.response.TokenResponse;
import com.peakle.shuttle.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Long>> signup(@Valid @RequestBody SignupRequest request) {
        Long userId = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("userId", userId));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    //TO-DO
    @PostMapping("/login/oAuth/")
    public ResponseEntity<TokenResponse> oAuthLogin(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.oAuthLogin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        TokenResponse response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody TokenRefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
