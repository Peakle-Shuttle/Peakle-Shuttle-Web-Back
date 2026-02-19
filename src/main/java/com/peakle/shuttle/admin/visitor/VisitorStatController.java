package com.peakle.shuttle.admin.visitor;

import com.peakle.shuttle.admin.visitor.dto.response.VisitorStatResponse;
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
@RequestMapping("/api/admin/visitor")
@RequiredArgsConstructor
@Tag(name = "Admin Visitor", description = "관리자 방문자 통계 API")
public class VisitorStatController {

    private final VisitorStatService visitorStatService;

    @Operation(summary = "오늘 방문자 통계 조회", description = "오늘의 UV/PV 통계를 조회합니다.")
    @GetMapping("/today")
    public ResponseEntity<VisitorStatResponse> getTodayStat(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(visitorStatService.getTodayStat());
    }

    @Operation(summary = "특정 날짜 방문자 통계 조회", description = "특정 날짜의 UV/PV 통계를 조회합니다.")
    @GetMapping("/{date}")
    public ResponseEntity<VisitorStatResponse> getStatByDate(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", example = "2025-02-12")
            @PathVariable String date
    ) {
        return ResponseEntity.ok(visitorStatService.getStatByDate(date));
    }

    @Operation(summary = "최근 N일 방문자 통계 조회", description = "최근 N일간의 UV/PV 통계를 조회합니다.")
    @GetMapping("/recent")
    public ResponseEntity<List<VisitorStatResponse>> getRecentStats(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Parameter(description = "조회할 일수", example = "7")
            @RequestParam(defaultValue = "7") int days
    ) {
        return ResponseEntity.ok(visitorStatService.getRecentStats(days));
    }
}
