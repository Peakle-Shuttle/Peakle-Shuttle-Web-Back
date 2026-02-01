package com.peakle.shuttle.review.repository;

import com.peakle.shuttle.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByReviewCode(Long reviewCode);
}
