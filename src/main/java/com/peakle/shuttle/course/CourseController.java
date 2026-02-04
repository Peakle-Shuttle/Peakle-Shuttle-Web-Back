package com.peakle.shuttle.course;

import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import com.peakle.shuttle.course.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Tag(name = "Course", description = "노선/배차 조회 API")
public class CourseController {

    private final CourseService courseService;

    /**
     * 모든 노선과 배차 정보를 조회합니다.
     *
     * @param user 인증된 사용자 정보
     * @return 노선 목록
     */
    @Operation(summary = "노선 목록 조회", description = "모든 노선과 배차 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CourseListResponse>> getAllCourses(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(courseService.getAllCoursesWithDispatches());
    }

    /**
     * 특정 노선의 상세 정보를 조회합니다.
     *
     * @param user 인증된 사용자 정보
     * @param courseId 노선 ID
     * @return 노선 상세 정보
     */
    @Operation(summary = "노선 상세 조회", description = "특정 노선의 상세 정보와 배차 목록을 조회합니다.")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourseDetail(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(courseService.getCourseDetail(courseId));
    }

    /**
     * 특정 배차의 상세 정보를 조회합니다.
     *
     * @param user 인증된 사용자 정보
     * @param dispatchId 배차 ID
     * @return 배차 상세 정보
     */
    @Operation(summary = "배차 상세 조회", description = "특정 배차의 상세 정보를 조회합니다.")
    @GetMapping("/dispatch/{dispatchId}")
    public ResponseEntity<DispatchDetailResponse> getDispatchDetail(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long dispatchId
    ) {
        return ResponseEntity.ok(courseService.getDispatchDetail(dispatchId));
    }

    /**
     * 노선 즐겨찾기를 추가하거나 제거합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 즐겨찾기 요청 정보
     */
    @Operation(summary = "코스 즐겨찾기", description = "코스를 즐겨찾기에 추가하거나 제거합니다.")
    @PostMapping("/wish")
    public ResponseEntity<Void> toggleWish(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody WishCreateRequest request
    ) {
        courseService.toggleWish(user.code(), request);
        return ResponseEntity.noContent().build();
    }
}
