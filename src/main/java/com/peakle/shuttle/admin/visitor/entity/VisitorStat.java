package com.peakle.shuttle.admin.visitor.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_stats", uniqueConstraints = {
        @UniqueConstraint(columnNames = "stat_date")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitorStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_code")
    private Long statCode;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "unique_visitors", nullable = false)
    private Long uniqueVisitors;

    @Column(name = "page_views", nullable = false)
    private Long pageViews;

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
    public VisitorStat(LocalDate statDate, Long uniqueVisitors, Long pageViews) {
        this.statDate = statDate;
        this.uniqueVisitors = uniqueVisitors;
        this.pageViews = pageViews;
    }

    public void updateStats(Long uniqueVisitors, Long pageViews) {
        this.uniqueVisitors = uniqueVisitors;
        this.pageViews = pageViews;
    }
}
