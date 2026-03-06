package com.peakle.shuttle.admin.stats;

import com.peakle.shuttle.admin.stats.dto.response.PassengerStatsResponse;
import com.peakle.shuttle.admin.stats.dto.response.PerformanceStatsResponse;
import com.peakle.shuttle.admin.stats.dto.response.StatsDetailResponse;
import com.peakle.shuttle.admin.stats.dto.response.StatsSummaryResponse;
import com.peakle.shuttle.admin.stats.enums.StatsInterval;
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
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@Tag(name = "Admin Stats", description = "관리자 성과 분석 및 통계 API")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @Operation(summary = "통계 요약 조회", description = "선택 기간의 일별 통계 + 선택기간 합계를 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<StatsSummaryResponse> getStatsSummary(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminStatsService.getStatsSummary(startDate, endDate));
    }

    @Operation(summary = "일별 통계 상세 분석 조회", description = "선택 기간의 통계를 일별로 조회합니다.")
    @GetMapping("/detail/daily")
    public ResponseEntity<List<StatsDetailResponse>> getDailyDetail(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminStatsService.getDetailStats(startDate, endDate, StatsInterval.DAILY));
    }

    @Operation(summary = "주별 통계 상세 분석 조회", description = "선택 기간의 통계를 주별로 조회합니다.")
    @GetMapping("/detail/weekly")
    public ResponseEntity<List<StatsDetailResponse>> getWeeklyDetail(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminStatsService.getDetailStats(startDate, endDate, StatsInterval.WEEKLY));
    }

    @Operation(summary = "월별 통계 상세 분석 조회", description = "선택 기간의 통계를 월별로 조회합니다.")
    @GetMapping("/detail/monthly")
    public ResponseEntity<List<StatsDetailResponse>> getMonthlyDetail(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminStatsService.getDetailStats(startDate, endDate, StatsInterval.MONTHLY));
    }

    @Operation(summary = "일별 성과 분석 조회", description = "기간별 성과 분석 5개 지표를 일별로 조회합니다.")
    @GetMapping("/performance/daily")
    public ResponseEntity<PerformanceStatsResponse> getDailyPerformance(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminStatsService.getPerformanceStats(startDate, endDate, StatsInterval.DAILY));
    }

    @Operation(summary = "주별 성과 분석 조회", description = "기간별 성과 분석 5개 지표를 주별로 조회합니다.")
    @GetMapping("/performance/weekly")
    public ResponseEntity<PerformanceStatsResponse> getWeeklyPerformance(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminStatsService.getPerformanceStats(startDate, endDate, StatsInterval.WEEKLY));
    }

    @Operation(summary = "월별 성과 분석 조회", description = "기간별 성과 분석 5개 지표를 월별로 조회합니다.")
    @GetMapping("/performance/monthly")
    public ResponseEntity<PerformanceStatsResponse> getMonthlyPerformance(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminStatsService.getPerformanceStats(startDate, endDate, StatsInterval.MONTHLY));
    }

    @Operation(summary = "일별 탑승자 수 조회", description = "기간별 탑승자 수를 일별로 조회합니다.")
    @GetMapping("/passenger/daily")
    public ResponseEntity<List<PassengerStatsResponse>> getDailyPassenger(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminStatsService.getPassengerStats(startDate, endDate, StatsInterval.DAILY));
    }

    @Operation(summary = "주별 탑승자 수 조회", description = "기간별 탑승자 수를 주별로 조회합니다.")
    @GetMapping("/passenger/weekly")
    public ResponseEntity<List<PassengerStatsResponse>> getWeeklyPassenger(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminStatsService.getPassengerStats(startDate, endDate, StatsInterval.WEEKLY));
    }

    @Operation(summary = "월별 탑승자 수 조회", description = "기간별 탑승자 수를 월별로 조회합니다.")
    @GetMapping("/passenger/monthly")
    public ResponseEntity<List<PassengerStatsResponse>> getMonthlyPassenger(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(adminStatsService.getPassengerStats(startDate, endDate, StatsInterval.MONTHLY));
    }
}
