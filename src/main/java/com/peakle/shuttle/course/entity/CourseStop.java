package com.peakle.shuttle.course.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "course_stops",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_course_stop",
                columnNames = {"course_code", "stop_code"}
        ))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_stop_code")
    private Long courseStopCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_code", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_code", nullable = false)
    private Stop stop;

    @Column(name = "stop_order", nullable = false)
    private Integer stopOrder;

    @Column(name = "estimated_arrival")
    private Integer estimatedArrival;  // 출발지 기준 도착 예상 시간(분)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public CourseStop(Course course, Stop stop, Integer stopOrder, Integer estimatedArrival) {
        this.course = course;
        this.stop = stop;
        this.stopOrder = stopOrder;
        this.estimatedArrival = estimatedArrival;
    }

    // 연관관계 편의 메서드
    public void setCourse(Course course) {
        this.course = course;
        if (!course.getCourseStops().contains(this)) {
            course.getCourseStops().add(this);
        }
    }
}