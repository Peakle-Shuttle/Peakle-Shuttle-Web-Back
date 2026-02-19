package com.peakle.shuttle.reservation.entity;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.course.entity.Dispatch;
import com.peakle.shuttle.global.enums.ReservationStatus;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_code", nullable = false)
    private Dispatch dispatch;

    @Column(name = "reservation_count")
    private Integer reservationCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", length = 20)
    private ReservationStatus reservationStatus;

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
    public Reservation(User user, Dispatch dispatch, Integer reservationCount, ReservationStatus reservationStatus) {
        this.user = user;
        this.dispatch = dispatch;
        this.reservationCount = reservationCount;
        this.reservationStatus = reservationStatus;
    }

    public void updateReservationCount(Integer reservationCount) {
        this.reservationCount = reservationCount;
    }

    public void cancel() {
        this.reservationStatus = ReservationStatus.CANCELED;
    }
}
