package com.peakle.shuttle.admin.course;

import com.peakle.shuttle.admin.course.dto.*;
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
@RequestMapping("/api/admin/course")
@RequiredArgsConstructor
@Tag(name = "Admin Course", description = "관리자 노선/배차 관리 API")
public class AdminCourseController {

    private final AdminCourseService adminCourseService;

    @Operation(summary = "노선 등록", description = "새 노선을 등록합니다.")
    @PostMapping
    public ResponseEntity<AdminCourseResponse> createCourse(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody CourseCreateRequest request
    ) {
        return ResponseEntity.ok(adminCourseService.createCourse(request));
    }

    @Operation(summary = "노선 목록 조회", description = "전체 노선 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AdminCourseResponse>> getCourses(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminCourseService.getCourses());
    }

    @Operation(summary = "노선 삭제", description = "노선을 폐지합니다.")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long courseId
    ) {
        adminCourseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "노선 수정", description = "노선 정보를 수정합니다.")
    @PatchMapping("/{courseId}")
    public ResponseEntity<Void> updateCourse(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long courseId,
            @Valid @RequestBody CourseUpdateRequest request
    ) {
        adminCourseService.updateCourse(courseId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "배차 등록", description = "새 배차를 등록합니다.")
    @PostMapping("/dispatch")
    public ResponseEntity<AdminDispatchResponse> createDispatch(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody DispatchCreateRequest request
    ) {
        return ResponseEntity.ok(adminCourseService.createDispatch(request));
    }

    @Operation(summary = "배차 목록 조회", description = "특정 노선의 배차 목록을 조회합니다.")
    @GetMapping("/dispatch")
    public ResponseEntity<List<AdminDispatchResponse>> getDispatches(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam Long courseCode
    ) {
        return ResponseEntity.ok(adminCourseService.getDispatches(courseCode));
    }

    @Operation(summary = "배차 삭제", description = "배차를 삭제합니다.")
    @DeleteMapping("/dispatch")
    public ResponseEntity<Void> deleteDispatch(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam Long dispatchCode
    ) {
        adminCourseService.deleteDispatch(dispatchCode);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "배차 수정", description = "배차 정보를 수정합니다.")
    @PatchMapping("/dispatch")
    public ResponseEntity<Void> updateDispatch(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody DispatchUpdateRequest request
    ) {
        adminCourseService.updateDispatch(request);
        return ResponseEntity.noContent().build();
    }
}
