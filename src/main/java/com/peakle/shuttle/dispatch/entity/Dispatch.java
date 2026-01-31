package com.peakle.shuttle.dispatch.entity;

import com.peakle.shuttle.course.entity.Course;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "dispatches")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispatch_code")
    private Long dispatchCode;

    @Column(name = "dispatch_id", nullable = false, unique = true, length = 50)
    private String dispatchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_code", nullable = false)
    private Course course;

    @Column(name = "dispatch_start_time")
    private LocalTime dispatchStartTime;

    @Column(name = "dispatch_day", length = 20)
    private String dispatchDay;

    @Column(name = "dispatch_wish_count")
    private Integer dispatchWishCount;

    @Column(name = "dispatch_occupied")
    private Integer dispatchOccupied;

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
    public Dispatch(String dispatchId, Course course, LocalTime dispatchStartTime,
                    String dispatchDay, Integer dispatchWishCount, Integer dispatchOccupied) {
        this.dispatchId = dispatchId;
        this.course = course;
        this.dispatchStartTime = dispatchStartTime;
        this.dispatchDay = dispatchDay;
        this.dispatchWishCount = dispatchWishCount;
        this.dispatchOccupied = dispatchOccupied;
    }
}
