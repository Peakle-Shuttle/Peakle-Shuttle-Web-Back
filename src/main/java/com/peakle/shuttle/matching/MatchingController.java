package com.peakle.shuttle.matching;

import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import com.peakle.shuttle.matching.dto.request.MatchingCreateRequest;
import com.peakle.shuttle.matching.dto.request.MatchingUpdateRequest;
import com.peakle.shuttle.matching.dto.response.MatchingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matching")
@RequiredArgsConstructor
@Tag(name = "Matching", description = "전세버스 매칭 API")
public class MatchingController {

    private final MatchingService matchingService;

    @Operation(summary = "매칭 생성", description = "전세버스 매칭 요청을 생성합니다.")
    @PostMapping
    public ResponseEntity<MatchingResponse> createMatching(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody MatchingCreateRequest request
    ) {
        return ResponseEntity.ok(matchingService.createMatching(user.code(), request));
    }

    @Operation(summary = "매칭 조회", description = "특정 매칭 정보를 조회합니다.")
    @GetMapping("/{matchingCode}")
    public ResponseEntity<MatchingResponse> getMatching(
            @PathVariable Long matchingCode
    ) {
        return ResponseEntity.ok(matchingService.getMatching(matchingCode));
    }

    @Operation(summary = "내 매칭 목록 조회", description = "내 매칭 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MatchingResponse>> getMatchings(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(matchingService.getMatchings(user.code()));
    }

    @Operation(summary = "매칭 수정", description = "매칭 정보를 수정합니다.")
    @PatchMapping("/{matchingCode}")
    public ResponseEntity<Void> updateMatching(
            @PathVariable Long matchingCode,
            @Valid @RequestBody MatchingUpdateRequest request
    ) {
        matchingService.updateMatching(matchingCode, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "매칭 삭제", description = "매칭을 삭제합니다.")
    @DeleteMapping("/{matchingCode}")
    public ResponseEntity<Void> deleteMatching(
            @PathVariable Long matchingCode
    ) {
        matchingService.deleteMatching(matchingCode);
        return ResponseEntity.noContent().build();
    }
}
