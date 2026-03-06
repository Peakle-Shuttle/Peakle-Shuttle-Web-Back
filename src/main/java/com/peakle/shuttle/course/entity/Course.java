package com.peakle.shuttle.course.entity;

import com.peakle.shuttle.global.enums.CourseStatus;
import com.peakle.shuttle.school.entity.School;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_code")
    private Long courseCode;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(name = "course_seats")
    private Integer courseSeats;

    @Column(name = "course_duration")
    private Integer courseDuration;

    @Column(name = "course_cost")
    private Integer courseCost;

    @Column(name = "departure_name", length = 100)
    private String departureName;

    @Column(name = "departure_address", length = 200)
    private String departureAddress;

    @Column(name = "arrival_name", length = 100)
    private String arrivalName;

    @Column(name = "arrival_address", length = 200)
    private String arrivalAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_code")
    private School school;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_status", nullable = false)
    private CourseStatus courseStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 배차 목록
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Dispatch> dispatches = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (courseStatus == null) {
            courseStatus = CourseStatus.ENABLE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public Course(String courseName, Integer courseSeats,
                  Integer courseDuration, Integer courseCost, School school,
                  String departureName, String departureAddress,
                  String arrivalName, String arrivalAddress) {
        this.courseName = courseName;
        this.courseSeats = courseSeats;
        this.courseDuration = courseDuration;
        this.courseCost = courseCost;
        this.school = school;
        this.departureName = departureName;
        this.departureAddress = departureAddress;
        this.arrivalName = arrivalName;
        this.arrivalAddress = arrivalAddress;
    }

    // ===== 수정 메서드 =====

    public void updateCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void updateCourseSeats(Integer courseSeats) {
        this.courseSeats = courseSeats;
    }

    public void updateCourseDuration(Integer courseDuration) {
        this.courseDuration = courseDuration;
    }

    public void updateCourseCost(Integer courseCost) {
        this.courseCost = courseCost;
    }

    public void updateSchool(School school) {
        this.school = school;
    }

    public void updateDepartureName(String departureName) {
        this.departureName = departureName;
    }

    public void updateDepartureAddress(String departureAddress) {
        this.departureAddress = departureAddress;
    }

    public void updateArrivalName(String arrivalName) {
        this.arrivalName = arrivalName;
    }

    public void updateArrivalAddress(String arrivalAddress) {
        this.arrivalAddress = arrivalAddress;
    }

    public void disable() {
        this.courseStatus = CourseStatus.DISABLE;
    }

    public void enable() {
        this.courseStatus = CourseStatus.ENABLE;
    }
}
