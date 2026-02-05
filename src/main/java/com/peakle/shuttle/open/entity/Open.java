package com.peakle.shuttle.open.entity;

import com.peakle.shuttle.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "opens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Open {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_code")
    private Long openCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @Column(name = "open_content", columnDefinition = "TEXT")
    private String openContent;

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
    public Open(User user, String openContent) {
        this.user = user;
        this.openContent = openContent;
    }

    public void updateOpenContent(String openContent) {
        this.openContent = openContent;
    }
}
