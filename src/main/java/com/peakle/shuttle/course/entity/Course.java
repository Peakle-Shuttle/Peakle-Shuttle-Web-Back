package com.peakle.shuttle.course.entity;

import com.peakle.shuttle.school.entity.School;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Comparator;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_code")
    private School school;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 배차 목록
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<Dispatch> dispatches = new HashSet<>();

    // 정차지점 목록 (순서대로 정렬)
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stopOrder ASC")
    private Set<CourseStop> courseStops = new HashSet<>();

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
    public Course(String courseName, Integer courseSeats,
                  Integer courseDuration, Integer courseCost, School school) {
        this.courseName = courseName;
        this.courseSeats = courseSeats;
        this.courseDuration = courseDuration;
        this.courseCost = courseCost;
        this.school = school;
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

    public void clearCourseStops() {
        this.courseStops.clear();
    }

    // ===== 편의 메서드 =====

    // 정차지점 추가
    public void addCourseStop(Stop stop, int order, Integer estimatedArrival) {
        CourseStop courseStop = CourseStop.builder()
                .course(this)
                .stop(stop)
                .stopOrder(order)
                .estimatedArrival(estimatedArrival)
                .build();
        this.courseStops.add(courseStop);
    }

    // 출발지 조회
    public Stop getDepartureStop() {
        return courseStops.stream()
                .filter(cs -> cs.getStopOrder() == 1)
                .findFirst()
                .map(CourseStop::getStop)
                .orElse(null);
    }

    // 종점 조회
    public Stop getArrivalStop() {
        return courseStops.stream()
                .max(Comparator.comparing(CourseStop::getStopOrder))
                .map(CourseStop::getStop)
                .orElse(null);
    }
}
