package com.peakle.shuttle.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Refresh Token 정보를 관리하는 JPA 엔티티 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long userCode;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Builder
    public RefreshToken(String token, Long userCode, LocalDateTime expiryDate) {
        this.token = token;
        this.userCode = userCode;
        this.expiryDate = expiryDate;
    }

    /** 토큰 값과 만료일을 갱신합니다. */
    public void updateToken(String token, LocalDateTime expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }

    /** 토큰이 만료되었는지 확인합니다. */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
