package com.peakle.shuttle.review;

import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import com.peakle.shuttle.review.dto.request.ReviewCreateRequest;
import com.peakle.shuttle.review.dto.request.ReviewUpdateRequest;
import com.peakle.shuttle.review.dto.response.ReviewListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Tag(name = "Review", description = "구매평 API")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 특정 노선의 구매평을 조회합니다.
     *
     * @param user 인증된 사용자 정보
     * @param courseCode 노선 코드
     * @return 구매평 목록
     */
    @Operation(summary = "구매평 조회", description = "특정 노선의 구매평을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReviewListResponse>> getReviews(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam Long courseCode
    ) {
        return ResponseEntity.ok(reviewService.getReviewsByCourse(courseCode));
    }

    /**
     * 구매평을 작성합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 구매평 작성 요청 정보
     */
    @Operation(summary = "구매평 작성", description = "구매평을 작성합니다.")
    @PostMapping
    public ResponseEntity<Void> createReview(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        reviewService.createReview(user.code(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 구매평을 수정합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 구매평 수정 요청 정보
     */
    @Operation(summary = "구매평 수정", description = "구매평을 수정합니다.")
    @PatchMapping
    public ResponseEntity<Void> updateReview(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody ReviewUpdateRequest request
    ) {
        reviewService.updateReview(user.code(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 구매평을 삭제합니다.
     *
     * @param user 인증된 사용자 정보
     * @param reviewCode 구매평 코드
     */
    @Operation(summary = "구매평 삭제", description = "구매평을 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteReview(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam Long reviewCode
    ) {
        reviewService.deleteReview(user.code(), reviewCode);
        return ResponseEntity.noContent().build();
    }
}
