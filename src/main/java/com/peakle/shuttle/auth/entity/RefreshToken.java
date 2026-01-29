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

    /**
     * 주어진 토큰 값, 사용자 코드 및 만료 시점으로 RefreshToken 엔티티 인스턴스를 생성한다.
     *
     * @param token      저장할 리프레시 토큰 문자열(중복 불가, null 아님)
     * @param userCode   토큰에 연결된 사용자 식별자(Null 불가)
     * @param expiryDate 토큰의 만료 시점(Null 불가)
     */
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

    /**
     * 현재 시각이 만료일을 지났는지 판별한다.
     *
     * @return `true`이면 만료일이 현재 시각보다 이전(토큰이 만료된 상태), `false`이면 그렇지 않음.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}