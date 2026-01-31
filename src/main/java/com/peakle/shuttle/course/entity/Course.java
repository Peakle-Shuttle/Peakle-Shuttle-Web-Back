package com.peakle.shuttle.course.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_code")
    private Long courseCode;

    @Column(name = "course_id", nullable = false, unique = true, length = 50)
    private String courseId;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(name = "course_start", length = 100)
    private String courseStart;

    @Column(name = "course_destination", length = 100)
    private String courseDestination;

    @Column(name = "course_seats")
    private Integer courseSeats;

    @Column(name = "course_duration")
    private Integer courseDuration;

    @Column(name = "course_cost")
    private Integer courseCost;

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
    public Course(String courseId, String courseName, String courseStart,
                  String courseDestination, Integer courseSeats,
                  Integer courseDuration, Integer courseCost) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseStart = courseStart;
        this.courseDestination = courseDestination;
        this.courseSeats = courseSeats;
        this.courseDuration = courseDuration;
        this.courseCost = courseCost;
    }
}
