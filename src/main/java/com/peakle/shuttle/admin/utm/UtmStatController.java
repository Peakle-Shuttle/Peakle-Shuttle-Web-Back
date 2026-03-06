package com.peakle.shuttle.admin.utm;

import com.peakle.shuttle.admin.utm.dto.response.UtmStatResponse;
import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/utm")
@RequiredArgsConstructor
@Tag(name = "Admin UTM", description = "관리자 UTM 추적 API")
public class UtmStatController {

    private final UtmStatService utmStatService;

    /**
     * 전체 UTM 통계 조회
     */
    @Operation(summary = "UTM 통계 전체 조회", description = "모든 UTM 통계를 카운트 내림차순으로 조회합니다.")
    @GetMapping
    public ResponseEntity<List<UtmStatResponse>> getAllUtmStats(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(utmStatService.getAllUtmStats());
    }

    /**
     * 특정 소스의 UTM 통계 조회
     */
    @Operation(summary = "소스별 UTM 통계 조회", description = "특정 소스의 UTM 통계를 조회합니다.")
    @GetMapping("/source/{source}")
    public ResponseEntity<List<UtmStatResponse>> getUtmStatsBySource(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Parameter(description = "UTM 소스", example = "google")
            @PathVariable String source
    ) {
        return ResponseEntity.ok(utmStatService.getUtmStatsBySource(source));
    }
}
