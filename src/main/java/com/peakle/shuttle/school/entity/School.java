package com.peakle.shuttle.school.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "schools")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_code")
    private Long schoolCode;

    @Column(name = "school_name", nullable = false, length = 100)
    private String schoolName;

    @Column(name = "school_address", length = 200)
    private String schoolAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public School(String schoolName, String schoolAddress) {
        this.schoolName = schoolName;
        this.schoolAddress = schoolAddress;
    }

    public void updateSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void updateSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }
}