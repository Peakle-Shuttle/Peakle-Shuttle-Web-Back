package com.peakle.shuttle.school;

import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import com.peakle.shuttle.school.dto.request.*;
import com.peakle.shuttle.school.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
@Tag(name = "School", description = "학교 관리 API")
public class SchoolController {

    private final SchoolService schoolService;

    /**
     * 모든 학교 목록을 조회합니다.
     */
    @Operation(summary = "학교 목록 조회", description = "모든 학교 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<SchoolResponse>> getAllSchools() {
        return ResponseEntity.ok(schoolService.getAllSchools());
    }

    /**
     * 특정 학교 정보를 조회합니다.
     */
    @Operation(summary = "학교 상세 조회", description = "특정 학교의 상세 정보를 조회합니다.")
    @GetMapping("/{schoolCode}")
    public ResponseEntity<SchoolResponse> getSchool(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long schoolCode
    ) {
        return ResponseEntity.ok(schoolService.getSchool(schoolCode));
    }

    /**
     * 학교를 생성합니다.
     */
    @Operation(summary = "학교 생성", description = "새로운 학교를 생성합니다.")
    @PostMapping
    public ResponseEntity<SchoolResponse> createSchool(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody SchoolCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(schoolService.createSchool(request));
    }

    /**
     * 학교 정보를 수정합니다.
     */
    @Operation(summary = "학교 수정", description = "학교 정보를 수정합니다.")
    @PutMapping("/{schoolCode}")
    public ResponseEntity<SchoolResponse> updateSchool(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long schoolCode,
            @Valid @RequestBody SchoolUpdateRequest request
    ) {
        return ResponseEntity.ok(schoolService.updateSchool(schoolCode, request));
    }

    /**
     * 학교를 삭제합니다.
     */
    @Operation(summary = "학교 삭제", description = "학교를 삭제합니다.")
    @DeleteMapping("/{schoolCode}")
    public ResponseEntity<Void> deleteSchool(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long schoolCode
    ) {
        schoolService.deleteSchool(schoolCode);
        return ResponseEntity.noContent().build();
    }
}
