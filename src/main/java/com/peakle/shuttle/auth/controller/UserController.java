package com.peakle.shuttle.auth.controller;

import com.peakle.shuttle.auth.dto.request.*;
import com.peakle.shuttle.auth.dto.response.UserClientResponse;
import com.peakle.shuttle.auth.service.UserService;
import com.peakle.shuttle.core.annotation.SignUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name =  "User", description = "회원 정보 조회/수정/삭제, 비밀번호 재설정 API")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "회원 정보 조회",
            description = "회원 정보를 조회합니다."
    )
    @GetMapping("/info")
    public ResponseEntity<UserClientResponse> getMyInfo(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
            ) {
        var responseBody = userService.getInfo(user.code());

        return ResponseEntity.ok()
                .eTag("\"" + user.code() + "\"")
                .body(responseBody);
    }

    @Operation(
            summary = "아이디 중복 확인",
            description = "해당 아이디가 이미 존재하는지 확인합니다."
    )
    @GetMapping("/info/id")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
        return ResponseEntity.ok(userService.existsByUserId(userId));
    }

    @Operation(
            summary = "이메일로 아이디 찾기",
            description = "이메일을 통해 해당 유저의 아이디를 조회합니다."
    )
    @GetMapping("/info/email")
    public ResponseEntity<String> findUserIdByEmail(@RequestParam String userEmail) {
        return ResponseEntity.ok(userService.findUserIdByEmail(userEmail));
    }

    @Operation(
            summary = "유저 정보 Update API - TO-DO",
            description = "회원 정보 수정 (핸드폰, 학교, 전공 ONLY)"
    )
    @PatchMapping("/info")
    public ResponseEntity<Void> updateInfo(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody UserInfoRequest userInfoRequest
            ) {
        userService.updateInfo(user.code(), userInfoRequest);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "아이디 변경",
            description = "새로운 아이디로 변경합니다."
    )
    @PatchMapping("/info/id")
    public ResponseEntity<Void> changeId(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody UserIdRequest request
            ) {
        userService.changeId(user.code(), request);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "이메일 변경",
            description = "새로운 이메일로 변경합니다."
    )
    @PatchMapping("/info/email")
    public ResponseEntity<Void> changeEmail(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody UserEmailRequest request
            ) {
        userService.changeEmail(user.code(), request);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "비밀번호 재설정",
            description = "새로운 비밀번호로 재설정합니다."
    )
    @PatchMapping("/info/pw")
    public ResponseEntity<Void> changePw(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody UserPwRequest request
            ) {
        userService.changePassword(user.code(), request);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "유저 삭제 API / 회원 탈퇴",
            description = "DB에서 회원 정보를 삭제합니다."
    )
    @DeleteMapping("/info")
    public ResponseEntity<Void> removeUser(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        userService.removeUser(user.code());
        return ResponseEntity.noContent().build();
    }
}
