package com.peakle.shuttle.reservation.entity;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.course.entity.Dispatch;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_code")
    private Long reservationCode;

    @Column(name = "reservation_id", nullable = false, unique = true, length = 50)
    private String reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_code", nullable = false)
    private Dispatch dispatch;

    @Column(name = "reservation_count")
    private Integer reservationCount;

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
    public Reservation(String reservationId, User user, Dispatch dispatch, Integer reservationCount) {
        this.reservationId = reservationId;
        this.user = user;
        this.dispatch = dispatch;
        this.reservationCount = reservationCount;
    }
}
