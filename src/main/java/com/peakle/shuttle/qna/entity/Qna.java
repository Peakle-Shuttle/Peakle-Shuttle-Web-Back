package com.peakle.shuttle.qna.entity;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.global.enums.QnaStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "qna_status", length = 20)
    private QnaStatus qnaState;

    @Column(name = "qna_image", length = 500)
    private String qnaImage;

    @OneToMany(mappedBy = "qna", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QnaComment> comments = new ArrayList<>();

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
               Boolean qnaIsPrivate, String qnaContent, QnaStatus qnaState, String qnaImage) {
        this.user = user;
        this.qnaTitle = qnaTitle;
        this.qnaDate = qnaDate;
        this.qnaIsPrivate = qnaIsPrivate;
        this.qnaContent = qnaContent;
        this.qnaState = qnaState;
        this.qnaImage = qnaImage;
    }

    public void updateQnaState(QnaStatus qnaState) {
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
