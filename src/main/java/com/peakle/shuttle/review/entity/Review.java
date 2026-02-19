package com.peakle.shuttle.review.entity;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.course.entity.Course;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_code")
    private Long reviewCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_code", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @Column(name = "review_content", columnDefinition = "TEXT")
    private String reviewContent;

    @Column(name = "review_image", length = 500)
    private String reviewImage;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public Review(Course course, User user,
                  LocalDateTime reviewDate, String reviewContent, String reviewImage) {
        this.course = course;
        this.user = user;
        this.reviewDate = reviewDate;
        this.reviewContent = reviewContent;
        this.reviewImage = reviewImage;
    }

    public void updateReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public void updateReviewImage(String reviewImage) {
        this.reviewImage = reviewImage;
    }
}
