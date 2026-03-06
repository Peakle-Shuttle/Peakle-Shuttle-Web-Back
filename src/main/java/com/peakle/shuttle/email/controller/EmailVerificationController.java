package com.peakle.shuttle.email.controller;

import com.peakle.shuttle.email.dto.request.*;
import com.peakle.shuttle.email.dto.response.FindIdResponse;
import com.peakle.shuttle.email.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 인증 코드 발송/검증 API")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @Operation(summary = "인증 코드 발송", description = "이메일로 6자리 인증 코드를 발송합니다.")
    @PostMapping("/send-code")
    public ResponseEntity<Void> sendCode(@Valid @RequestBody EmailSendCodeRequest request) {
        emailVerificationService.sendVerificationCode(request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "인증 코드 검증", description = "발송된 인증 코드를 검증합니다.")
    @PostMapping("/verify-code")
    public ResponseEntity<Void> verifyCode(@Valid @RequestBody EmailVerifyCodeRequest request) {
        emailVerificationService.verifyCode(request.email(), request.code());
        return ResponseEntity.ok().build();
    }

    // === 아이디 찾기 ===

    @Operation(summary = "아이디 찾기 - 인증 코드 발송", description = "이름, 전화번호, 이메일이 일치하는 사용자에게 인증 코드를 발송합니다.")
    @PostMapping("/find-id/send-code")
    public ResponseEntity<Void> sendFindIdCode(@Valid @RequestBody FindIdSendCodeRequest request) {
        emailVerificationService.sendFindIdCode(request.userName(), request.userNumber(), request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "아이디 찾기 - 인증 코드 검증", description = "인증 코드 검증 후 아이디를 반환합니다.")
    @PostMapping("/find-id/verify-code")
    public ResponseEntity<FindIdResponse> verifyFindIdCode(@Valid @RequestBody EmailVerifyCodeRequest request) {
        String userId = emailVerificationService.verifyFindIdCode(request.email(), request.code());
        return ResponseEntity.ok(new FindIdResponse(userId));
    }

    // === 비밀번호 재설정 ===

    @Operation(summary = "비밀번호 재설정 - 인증 코드 발송", description = "이름, 전화번호, 아이디, 이메일이 일치하는 사용자에게 인증 코드를 발송합니다.")
    @PostMapping("/reset-pw/send-code")
    public ResponseEntity<Void> sendResetPwCode(@Valid @RequestBody ResetPwSendCodeRequest request) {
        emailVerificationService.sendResetPwCode(request.userName(), request.userNumber(), request.userId(), request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 재설정 - 인증 코드 검증", description = "인증 코드를 검증합니다.")
    @PostMapping("/reset-pw/verify-code")
    public ResponseEntity<Void> verifyResetPwCode(@Valid @RequestBody EmailVerifyCodeRequest request) {
        emailVerificationService.verifyResetPwCode(request.email(), request.code());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 재설정", description = "이메일 인증 완료 후 새 비밀번호를 설정합니다.")
    @PostMapping("/reset-pw")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPwRequest request) {
        emailVerificationService.resetPassword(request.email(), request.newPassword());
        return ResponseEntity.ok().build();
    }
}
