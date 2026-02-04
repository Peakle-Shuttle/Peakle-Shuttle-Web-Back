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

    /** 사용자 이름을 변경합니다. */
    public void updateUserId(String userId) {
        this.userId = userId;
    }

    /** 사용자 이메일을 변경합니다. */
    public void updateUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /** 비밀번호를 변경합니다. */
    public void updatePassword(String userPassword) { this.userPassword = userPassword; }


    public void updateUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public void updateUserSchool(String userSchool) {
        this.userSchool = userSchool;
    }

    public void updateUserMajor(String userMajor) {
        this.userMajor = userMajor;
    }

    public void updateUserBirth(LocalDate userBirth) {
        this.userBirth = userBirth;
    }

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
