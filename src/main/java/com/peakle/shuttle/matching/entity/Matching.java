package com.peakle.shuttle.matching.entity;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.global.enums.MatchingStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "matchings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_code")
    private Long matchingCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code", nullable = false)
    private User user;

    @Column(name = "customer_type")
    private String customerType;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "manager_name")
    private String managerName;

    @Column(name = "email")
    private String email;

    @Column(name = "contact")
    private String contact;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "departure_date_time")
    private String departureDateTime;

    @Column(name = "departure")
    private String departure;

    @Column(name = "destination")
    private String destination;

    @Column(name = "estimated_passengers")
    private String estimatedPassengers;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "required_documents")
    private String requiredDocuments;

    @Column(name = "additional_requests")
    private String additionalRequests;

    @Column(name = "privacy_consent")
    private String privacyConsent;

    @Enumerated(EnumType.STRING)
    @Column(name = "matching_status", nullable = false)
    private MatchingStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = MatchingStatus.WAITING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public Matching(User user, String customerType, String organizationName, String managerName,
                    String email, String contact, String purpose, String departureDateTime,
                    String departure, String destination, String estimatedPassengers, String vehicleType,
                    String operationType, String requiredDocuments, String additionalRequests,
                    String privacyConsent) {
        this.user = user;
        this.customerType = customerType;
        this.organizationName = organizationName;
        this.managerName = managerName;
        this.email = email;
        this.contact = contact;
        this.purpose = purpose;
        this.departureDateTime = departureDateTime;
        this.departure = departure;
        this.destination = destination;
        this.estimatedPassengers = estimatedPassengers;
        this.vehicleType = vehicleType;
        this.operationType = operationType;
        this.requiredDocuments = requiredDocuments;
        this.additionalRequests = additionalRequests;
        this.privacyConsent = privacyConsent;
    }

    public void update(String customerType, String organizationName, String managerName,
                       String email, String contact, String purpose, String departureDateTime,
                       String departure, String destination, String estimatedPassengers, String vehicleType,
                       String operationType, String requiredDocuments, String additionalRequests,
                       String privacyConsent) {
        if (customerType != null) this.customerType = customerType;
        if (organizationName != null) this.organizationName = organizationName;
        if (managerName != null) this.managerName = managerName;
        if (email != null) this.email = email;
        if (contact != null) this.contact = contact;
        if (purpose != null) this.purpose = purpose;
        if (departureDateTime != null) this.departureDateTime = departureDateTime;
        if (departure != null) this.departure = departure;
        if (destination != null) this.destination = destination;
        if (estimatedPassengers != null) this.estimatedPassengers = estimatedPassengers;
        if (vehicleType != null) this.vehicleType = vehicleType;
        if (operationType != null) this.operationType = operationType;
        if (requiredDocuments != null) this.requiredDocuments = requiredDocuments;
        if (additionalRequests != null) this.additionalRequests = additionalRequests;
        if (privacyConsent != null) this.privacyConsent = privacyConsent;
    }

    public void process() {
        this.status = MatchingStatus.PROCESSING;
    }

    public void complete() {
        this.status = MatchingStatus.COMPLETED;
    }

    public void softDelete() {
        this.status = MatchingStatus.DELETED;
    }
}
