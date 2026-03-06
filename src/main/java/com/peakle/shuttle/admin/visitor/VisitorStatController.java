package com.peakle.shuttle.admin.visitor;

import com.peakle.shuttle.admin.stats.enums.StatsInterval;
import com.peakle.shuttle.admin.visitor.dto.response.VisitorStatResponse;
import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/visitor")
@RequiredArgsConstructor
@Tag(name = "Admin Visitor", description = "관리자 방문자 통계 API")
public class VisitorStatController {

    private final VisitorStatService visitorStatService;

    @Operation(summary = "최근 N일 방문자 통계 조회", description = "최근 N일간의 UV/PV 통계를 조회합니다.")
    @GetMapping("/recent")
    public ResponseEntity<List<VisitorStatResponse>> getRecentStats(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Parameter(description = "조회할 일수", example = "7")
            @RequestParam(defaultValue = "7") int days
    ) {
        return ResponseEntity.ok(visitorStatService.getRecentStats(days));
    }

    @Operation(summary = "일별 방문자 통계 조회", description = "기간별 UV/PV 통계를 일별로 조회합니다.")
    @GetMapping("/daily")
    public ResponseEntity<List<VisitorStatResponse>> getDailyStats(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(visitorStatService.getStatsByPeriod(startDate, endDate, StatsInterval.DAILY));
    }

    @Operation(summary = "주별 방문자 통계 조회", description = "기간별 UV/PV 통계를 주별로 조회합니다.")
    @GetMapping("/weekly")
    public ResponseEntity<List<VisitorStatResponse>> getWeeklyStats(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(visitorStatService.getStatsByPeriod(startDate, endDate, StatsInterval.WEEKLY));
    }

    @Operation(summary = "월별 방문자 통계 조회", description = "기간별 UV/PV 통계를 월별로 조회합니다.")
    @GetMapping("/monthly")
    public ResponseEntity<List<VisitorStatResponse>> getMonthlyStats(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(visitorStatService.getStatsByPeriod(startDate, endDate, StatsInterval.MONTHLY));
    }
}
