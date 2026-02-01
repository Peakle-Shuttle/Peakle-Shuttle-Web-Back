package com.peakle.shuttle.review.entity;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.course.entity.Dispatch;
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
    @JoinColumn(name = "dispatch_code", nullable = false)
    private Dispatch dispatch;

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
    public Review(Dispatch dispatch, User user,
                  LocalDateTime reviewDate, String reviewContent, String reviewImage) {
        this.dispatch = dispatch;
        this.user = user;
        this.reviewDate = reviewDate;
        this.reviewContent = reviewContent;
        this.reviewImage = reviewImage;
    }
}
