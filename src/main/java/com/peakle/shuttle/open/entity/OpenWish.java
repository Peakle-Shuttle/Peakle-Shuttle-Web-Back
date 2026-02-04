package com.peakle.shuttle.open.entity;

import com.peakle.shuttle.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "open_wishes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenWish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_code")
    private Long wishCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_code", nullable = false)
    private Open open;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    private String openWishContent;

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
    public OpenWish(Open open, User user, String openWishContent) {
        this.open = open;
        this.user = user;
        this.openWishContent = openWishContent;
    }
}
