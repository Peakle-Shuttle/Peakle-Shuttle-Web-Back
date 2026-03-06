package com.peakle.shuttle.admin.utm.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "utm_stats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"utm_source", "utm_medium", "utm_campaign"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UtmStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "utm_id")
    private Long utmId;

    @Column(name = "utm_source", nullable = false, length = 100)
    private String utmSource;

    @Column(name = "utm_medium", nullable = false, length = 100)
    private String utmMedium;

    @Column(name = "utm_campaign", nullable = false, length = 100)
    private String utmCampaign;

    @Column(name = "utm_count", nullable = false)
    private Long utmCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (utmCount == null) {
            utmCount = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public UtmStat(String utmSource, String utmMedium, String utmCampaign, Long utmCount) {
        this.utmSource = utmSource;
        this.utmMedium = utmMedium;
        this.utmCampaign = utmCampaign;
        this.utmCount = utmCount != null ? utmCount : 1L;
    }

    public void incrementCount() {
        this.utmCount++;
    }
}
