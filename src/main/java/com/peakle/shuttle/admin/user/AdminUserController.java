package com.peakle.shuttle.admin.user;

import com.peakle.shuttle.admin.user.dto.AdminUserListResponse;
import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
@Tag(name = "Admin User", description = "관리자 고객 관리 API")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 민감 정보를 제외한 사용자 목록을 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @return 사용자 목록
     */
    @Operation(summary = "고객 목록 조회", description = "민감 정보를 제외한 사용자 정보 리스트를 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<AdminUserListResponse>> getUsers(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminUserService.getUsers());
    }
}
