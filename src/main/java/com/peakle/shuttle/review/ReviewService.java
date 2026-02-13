package com.peakle.shuttle.review;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.course.entity.Course;
import com.peakle.shuttle.course.repository.CourseRepository;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.review.dto.request.ReviewCreateRequest;
import com.peakle.shuttle.review.dto.request.ReviewUpdateRequest;
import com.peakle.shuttle.review.dto.response.ReviewListResponse;
import com.peakle.shuttle.review.entity.Review;
import com.peakle.shuttle.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    /**
     * 특정 노선의 구매평 목록을 조회합니다.
     *
     * @param courseCode 노선 코드
     * @return 구매평 목록
     */
    public List<ReviewListResponse> getReviewsByCourse(Long courseCode) {
        return reviewRepository.findAllByCourseCodeWithUser(courseCode).stream()
                .map(ReviewListResponse::from)
                .toList();
    }

    /**
     * 구매평을 작성합니다.
     *
     * @param userCode 사용자 코드
     * @param request 구매평 작성 요청 정보
     * @throws AuthException 사용자 또는 노선을 찾을 수 없는 경우
     */
    @Transactional
    public void createReview(Long userCode, ReviewCreateRequest request) {
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));
        Course course = courseRepository.findByCourseCode(request.courseCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_COURSE));

        Review review = Review.builder()
                .course(course)
                .user(user)
                .reviewDate(LocalDateTime.now())
                .reviewContent(request.reviewContent())
                .reviewImage(request.reviewImage())
                .build();

        reviewRepository.save(review);
    }

    /**
     * 구매평을 수정합니다. null이 아닌 필드만 업데이트됩니다.
     *
     * @param userCode 사용자 코드
     * @param request 구매평 수정 요청 정보
     * @throws AuthException 구매평을 찾을 수 없는 경우
     */
    @Transactional
    public void updateReview(Long userCode, ReviewUpdateRequest request) {
        Review review = reviewRepository.findByReviewCodeAndUserUserCode(request.reviewCode(), userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_REVIEW));

        if (request.reviewContent() != null) review.updateReviewContent(request.reviewContent());
        if (request.reviewImage() != null) review.updateReviewImage(request.reviewImage());
    }

    /**
     * 구매평을 삭제합니다.
     *
     * @param userCode 사용자 코드
     * @param reviewCode 구매평 코드
     * @throws AuthException 구매평을 찾을 수 없는 경우
     */
    @Transactional
    public void deleteReview(Long userCode, Long reviewCode) {
        Review review = reviewRepository.findByReviewCodeAndUserUserCode(reviewCode, userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_REVIEW));
        reviewRepository.delete(review);
    }
}
