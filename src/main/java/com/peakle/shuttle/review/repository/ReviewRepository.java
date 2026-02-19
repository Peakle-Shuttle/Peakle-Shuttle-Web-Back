package com.peakle.shuttle.review.repository;

import com.peakle.shuttle.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByReviewCode(Long reviewCode);

    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.course.courseCode = :courseCode")
    List<Review> findAllByCourseCodeWithUser(@Param("courseCode") Long courseCode);

    Optional<Review> findByReviewCodeAndUserUserCode(Long reviewCode, Long userCode);
}
