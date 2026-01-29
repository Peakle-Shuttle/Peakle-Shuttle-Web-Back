package com.peakle.shuttle.auth.entity;

import com.peakle.shuttle.auth.dto.request.UserInfoRequest;
import com.peakle.shuttle.global.enums.AuthProvider;
import com.peakle.shuttle.global.enums.Role;
import com.peakle.shuttle.global.enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 사용자 정보를 관리하는 JPA 엔티티 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_code")
    private Long userCode;

    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String
            userId;

    @Column(name = "user_password", length = 100)
    private String userPassword;

    @Column(name = "user_email", length = 100)
    private String userEmail;

    @Column(name = "user_name", length = 50)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private Role userRole;

    @Column(name = "user_gender", length = 10)
    private String userGender;

    @Column(name = "user_number", length = 20)
    private String userNumber;

    @Column(name = "user_birth")
    private LocalDate userBirth;

    @Column(name = "user_school", length = 100)
    private String userSchool;

    @Column(name = "user_major", length = 100)
    private String userMajor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(length = 100)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private Status status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * 엔티티가 처음 영속화되기 전에 생성 시각(createdAt)과 수정 시각(updatedAt)을 현재 시각으로 초기화한다.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티가 갱신되기 직전에 `updatedAt` 필드를 현재 시간으로 갱신한다.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 사용자 엔티티의 필수 속성으로 새 User 인스턴스를 생성하고 상태를 ACTIVE로 초기화한다.
     *
     * @param userId       고유 로그인 아이디
     * @param userPassword 암호화된 사용자 비밀번호
     * @param userEmail    사용자 이메일 주소
     * @param userName     사용자 표시 이름
     * @param userRole     사용자 권한(Role)
     * @param userGender   성별 정보(예: "male", "female" 등)
     * @param userNumber   연락처 번호
     * @param userBirth    생년월일
     * @param userSchool   소속 학교명
     * @param userMajor    전공명
     * @param provider     인증 제공자(AuthProvider)
     * @param providerId   인증 제공자에서 발급한 사용자 식별자
     */
    @Builder
    public User(String userId, String userPassword, String userEmail, String userName,
                Role userRole, String userGender, String userNumber, LocalDate userBirth,
                String userSchool, String userMajor, AuthProvider provider, String providerId) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userRole = userRole;
        this.userGender = userGender;
        this.userNumber = userNumber;
        this.userBirth = userBirth;
        this.userSchool = userSchool;
        this.userMajor = userMajor;
        this.provider = provider;
        this.providerId = providerId;
        this.status = Status.ACTIVE;
    }

    /**
     * 사용자의 로그인 식별자(userId)를 변경한다.
     *
     * @param userId 변경할 새로운 로그인 식별자
     */
    public void updateUserId(String userId) {
        this.userId = userId;
    }

    /** 사용자 이메일을 변경합니다. */
    public void updateUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
 * 사용자의 비밀번호를 새 값으로 설정합니다.
 *
 * @param userPassword 설정할 새 비밀번호
 */
    public void updatePassword(String userPassword) { this.userPassword = userPassword; }


    /** 사용자를 소프트 삭제합니다 (상태를 DELETED로 변경). */
    public void deleteUser() {
        if (status == Status.DELETED) {
            return;
        }
        this.userName = "Deleted User";
        this.status = Status.DELETED;
        deletedAt = LocalDateTime.now();
    }

}