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

    /**
     * 새 노선을 등록합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param request 노선 생성 요청 정보
     * @return 등록된 노선 정보
     */
    @Operation(summary = "노선 등록", description = "새 노선을 등록합니다.")
    @PostMapping
    public ResponseEntity<AdminCourseResponse> createCourse(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody CourseCreateRequest request
    ) {
        return ResponseEntity.ok(adminCourseService.createCourse(request));
    }

    /**
     * 전체 노선 목록을 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @return 노선 목록
     */
    @Operation(summary = "노선 목록 조회", description = "전체 노선 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AdminCourseResponse>> getCourses(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminCourseService.getCourses());
    }

    /**
     * 노선을 삭제합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param courseId 삭제할 노선 ID
     */
    @Operation(summary = "노선 삭제", description = "노선을 폐지합니다.")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long courseId
    ) {
        adminCourseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 노선 정보를 수정합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param courseId 수정할 노선 ID
     * @param request 노선 수정 요청 정보
     */
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

    /**
     * 새 배차를 등록합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param request 배차 생성 요청 정보
     * @return 등록된 배차 정보
     */
    @Operation(summary = "배차 등록", description = "새 배차를 등록합니다.")
    @PostMapping("/dispatch")
    public ResponseEntity<AdminDispatchResponse> createDispatch(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody DispatchCreateRequest request
    ) {
        return ResponseEntity.ok(adminCourseService.createDispatch(request));
    }

    /**
     * 특정 노선의 배차 목록을 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param courseCode 노선 코드
     * @return 배차 목록
     */
    @Operation(summary = "배차 목록 조회", description = "특정 노선의 배차 목록을 조회합니다.")
    @GetMapping("/dispatch")
    public ResponseEntity<List<AdminDispatchResponse>> getDispatches(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam Long courseCode
    ) {
        return ResponseEntity.ok(adminCourseService.getDispatches(courseCode));
    }

    /**
     * 배차를 삭제합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param dispatchCode 삭제할 배차 코드
     */
    @Operation(summary = "배차 삭제", description = "배차를 삭제합니다.")
    @DeleteMapping("/dispatch")
    public ResponseEntity<Void> deleteDispatch(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam Long dispatchCode
    ) {
        adminCourseService.deleteDispatch(dispatchCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * 배차 정보를 수정합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param request 배차 수정 요청 정보
     */
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
