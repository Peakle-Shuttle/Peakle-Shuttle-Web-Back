package com.peakle.shuttle.review.dto.response;

import com.peakle.shuttle.review.entity.Review;

import java.time.LocalDateTime;

public record ReviewListResponse(
        Long reviewCode,
        String userName,
        LocalDateTime reviewDate,
        Integer rating,
        String reviewContent,
        String reviewImage
) {
    public static ReviewListResponse from(Review review) {
        return new ReviewListResponse(
                review.getReviewCode(),
                review.getUser().getUserName(),
                review.getReviewDate(),
                review.getRating(),
                review.getReviewContent(),
                review.getReviewImage()
        );
    }
}
