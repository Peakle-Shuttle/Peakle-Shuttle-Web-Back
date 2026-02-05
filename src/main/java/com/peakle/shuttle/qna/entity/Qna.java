package com.peakle.shuttle.qna.entity;

import com.peakle.shuttle.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "qnas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_code")
    private Long qnaCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @Column(name = "qna_title", length = 200)
    private String qnaTitle;

    @Column(name = "qna_date")
    private LocalDateTime qnaDate;

    @Column(name = "qna_is_private")
    private Boolean qnaIsPrivate;

    @Column(name = "qna_content", columnDefinition = "TEXT")
    private String qnaContent;

    @Column(name = "qna_state", length = 20)
    private String qnaState;

    @Column(name = "qna_image", length = 500)
    private String qnaImage;

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
    public Qna(User user, String qnaTitle, LocalDateTime qnaDate,
               Boolean qnaIsPrivate, String qnaContent, String qnaState, String qnaImage) {
        this.user = user;
        this.qnaTitle = qnaTitle;
        this.qnaDate = qnaDate;
        this.qnaIsPrivate = qnaIsPrivate;
        this.qnaContent = qnaContent;
        this.qnaState = qnaState;
        this.qnaImage = qnaImage;
    }

    public void updateQnaState(String qnaState) {
        this.qnaState = qnaState;
    }

    public void updateQnaTitle(String qnaTitle) {
        this.qnaTitle = qnaTitle;
    }

    public void updateQnaContent(String qnaContent) {
        this.qnaContent = qnaContent;
    }

    public void updateQnaIsPrivate(Boolean qnaIsPrivate) {
        this.qnaIsPrivate = qnaIsPrivate;
    }

    public void updateQnaImage(String qnaImage) {
        this.qnaImage = qnaImage;
    }
}
