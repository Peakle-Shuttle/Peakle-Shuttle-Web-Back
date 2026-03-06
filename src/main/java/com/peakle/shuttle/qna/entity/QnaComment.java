package com.peakle.shuttle.qna.entity;

import com.peakle.shuttle.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "qna_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QnaComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_code")
    private Long commentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_code", nullable = false)
    private Qna qna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @Column(name = "comment_date")
    private LocalDateTime commentDate;

    @Column(name = "comment_content", columnDefinition = "TEXT")
    private String commentContent;

    @Column(name = "comment_image", length = 500)
    private String commentImage;

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
    public QnaComment(Qna qna, User user, LocalDateTime commentDate,
                      String commentContent, String commentImage) {
        this.qna = qna;
        this.user = user;
        this.commentDate = commentDate;
        this.commentContent = commentContent;
        this.commentImage = commentImage;
    }
}
