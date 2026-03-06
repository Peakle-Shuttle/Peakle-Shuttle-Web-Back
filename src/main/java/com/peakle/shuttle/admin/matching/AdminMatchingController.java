package com.peakle.shuttle.admin.matching;

import com.peakle.shuttle.admin.matching.dto.request.AdminMatchingStatusUpdateRequest;
import com.peakle.shuttle.admin.matching.dto.response.AdminMatchingResponse;
import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/matching")
@RequiredArgsConstructor
@Tag(name = "Admin Matching", description = "관리자 매칭 관리 API")
public class AdminMatchingController {

    private final AdminMatchingService adminMatchingService;

    @Operation(summary = "매칭 목록 조회", description = "전체 매칭 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AdminMatchingResponse>> getMatchings(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminMatchingService.getAllMatchings());
    }

    @Operation(summary = "매칭 상세 조회", description = "특정 매칭의 상세 정보를 조회합니다.")
    @GetMapping("/{matchingCode}")
    public ResponseEntity<AdminMatchingResponse> getMatching(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long matchingCode
    ) {
        return ResponseEntity.ok(adminMatchingService.getMatching(matchingCode));
    }

    @Operation(summary = "매칭 상태 변경", description = "매칭 상태를 변경합니다. (PROCESSING, COMPLETED)")
    @PatchMapping("/{matchingCode}/status")
    public ResponseEntity<Void> updateMatchingStatus(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long matchingCode,
            @Valid @RequestBody AdminMatchingStatusUpdateRequest request
    ) {
        adminMatchingService.updateStatus(matchingCode, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "매칭 삭제", description = "매칭을 삭제합니다. (Soft Delete)")
    @DeleteMapping("/{matchingCode}")
    public ResponseEntity<Void> deleteMatching(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long matchingCode
    ) {
        adminMatchingService.deleteMatching(matchingCode);
        return ResponseEntity.noContent().build();
    }
}
