package com.peakle.shuttle.course.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispatches")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispatch_code")
    private Long dispatchCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_code", nullable = false)
    private Course course;

    @Column(name = "dispatch_datetime")
    private LocalDateTime dispatchDatetime;

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
    public Dispatch(Course course, LocalDateTime dispatchDatetime,
                    Integer dispatchWishCount, Integer dispatchOccupied) {
        this.course = course;
        this.dispatchDatetime = dispatchDatetime;
        this.dispatchWishCount = dispatchWishCount;
        this.dispatchOccupied = dispatchOccupied;
    }

    public void updateDatetime(LocalDateTime dispatchDatetime) {
        this.dispatchDatetime = dispatchDatetime;
    }

    public void incrementOccupied(Integer count) {
        if (this.dispatchOccupied == null) {
            this.dispatchOccupied = 0;
        }
        this.dispatchOccupied += count;
    }

    public void decrementOccupied(Integer count) {
        if (this.dispatchOccupied != null && this.dispatchOccupied >= count) {
            this.dispatchOccupied -= count;
        }
    }
}
