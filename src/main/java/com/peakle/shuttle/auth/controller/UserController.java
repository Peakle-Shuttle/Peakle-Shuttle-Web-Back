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

/** 회원 정보 조회/수정/삭제 및 비밀번호 재설정 API 컨트롤러 */
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
    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     *
     * @param user 인증된 사용자 정보
     * @return 사용자 상세 정보
     */
    @GetMapping("/info")
    public ResponseEntity<UserClientResponse> getMyInfo(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
            ) {
        var responseBody = userService.getInfo(user.code());

        return ResponseEntity.ok()
                .eTag("\"" + user.code() + "\"")
                .body(responseBody);
    }

    /**
     * 주어진 사용자 아이디의 중복 여부를 확인합니다.
     *
     * @param userId 확인할 사용자 아이디
     * @return `true`일 경우 해당 아이디가 이미 존재합니다, `false`일 경우 존재하지 않습니다.
     */
    @Operation(
            summary = "아이디 중복 확인",
            description = "해당 아이디가 이미 존재하는지 확인합니다."
    )
    /**
     * 아이디 중복 여부를 확인합니다.
     *
     * @param userId 확인할 사용자 ID
     * @return 중복이면 true
     */
    @GetMapping("/info/id")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
        return ResponseEntity.ok(userService.existsByUserId(userId));
    }

    @Operation(
            summary = "이메일로 아이디 찾기",
            description = "이메일을 통해 해당 유저의 아이디를 조회합니다."
    )
    /**
     * 이메일을 통해 해당 유저의 아이디를 조회합니다.
     *
     * @param userEmail 조회할 이메일
     * @return 해당 이메일로 등록된 사용자 ID
     */
    @GetMapping("/info/email")
    public ResponseEntity<String> findUserIdByEmail(@RequestParam String userEmail) {
        return ResponseEntity.ok(userService.findUserIdByEmail(userEmail));
    }

    /**
     * 인증된 사용자의 휴대전화, 학교, 전공 정보를 수정합니다.
     *
     * @param userInfoRequest 수정할 회원 정보(휴대전화, 학교, 전공)
     */
    @Operation(
            summary = "유저 정보 Update API - TO-DO",
            description = "회원 정보 수정 (핸드폰, 학교, 전공 ONLY)"
    )
    /**
     * 회원 정보를 수정합니다 (핸드폰, 학교, 전공).
     *
     * @param user 인증된 사용자 정보
     * @param userInfoRequest 수정할 회원 정보
     */
    @PatchMapping("/info")
    public ResponseEntity<Void> updateInfo(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody UserInfoRequest userInfoRequest
            ) {
        userService.updateInfo(user.code(), userInfoRequest);

        return ResponseEntity.noContent().build();
    }

    /**
     * 현재 인증된 사용자의 아이디를 새로운 아이디로 변경합니다.
     *
     * @param request 변경할 새로운 사용자 아이디를 담은 요청 DTO
     */
    @Operation(
            summary = "아이디 변경",
            description = "새로운 아이디로 변경합니다."
    )
    /**
     * 사용자 ID를 변경합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 변경할 ID 정보
     */
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
    /**
     * 사용자 이메일을 변경합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 변경할 이메일 정보
     */
    @PatchMapping("/info/email")
    public ResponseEntity<Void> changeEmail(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody UserEmailRequest request
            ) {
        userService.changeEmail(user.code(), request);

        return ResponseEntity.noContent().build();
    }

    /**
     * 인증된 사용자의 비밀번호를 새 값으로 재설정합니다.
     *
     * @param user    요청을 수행하는 인증된 사용자 정보
     * @param request 새 비밀번호 정보를 담은 요청 본문
     * @return        비밀번호 재설정이 성공하면 HTTP 204 No Content 응답
     */
    @Operation(
            summary = "비밀번호 재설정",
            description = "새로운 비밀번호로 재설정합니다."
    )
    /**
     * 비밀번호를 재설정합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 새 비밀번호 정보
     */
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
    /**
     * 회원 탈퇴를 진행합니다 (소프트 삭제).
     *
     * @param user 인증된 사용자 정보
     */
    @DeleteMapping("/info")
    public ResponseEntity<Void> removeUser(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        userService.removeUser(user.code());
        return ResponseEntity.noContent().build();
    }
}