package com.peakle.shuttle.course.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stops")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stop_code")
    private Long stopCode;

    @Column(name = "stop_id", nullable = false, unique = true, length = 50)
    private String stopId;

    @Column(name = "stop_name", nullable = false, length = 100)
    private String stopName;

    @Column(name = "stop_address", length = 200)
    private String stopAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 연관관계 (양방향 필요시)
    @OneToMany(mappedBy = "stop")
    private List<CourseStop> courseStops = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.stopId == null) {
            this.stopId = "STOP-" + UUID.randomUUID().toString().substring(0, 8);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Stop(String stopId, String stopName, String stopAddress) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopAddress = stopAddress;
    }
}