package com.peakle.shuttle.matching.dto.response;

import com.peakle.shuttle.global.enums.MatchingStatus;
import com.peakle.shuttle.matching.entity.Matching;

import java.time.LocalDateTime;

public record MatchingResponse(
        Long matchingCode,
        MatchingStatus status,
        String customerType,
        String organizationName,
        String managerName,
        String email,
        String contact,
        String purpose,
        String departureDateTime,
        String departure,
        String destination,
        String estimatedPassengers,
        String vehicleType,
        String operationType,
        String requiredDocuments,
        String additionalRequests,
        String privacyConsent,
        LocalDateTime createdAt
) {
    public static MatchingResponse from(Matching matching) {
        return new MatchingResponse(
                matching.getMatchingCode(),
                matching.getStatus(),
                matching.getCustomerType(),
                matching.getOrganizationName(),
                matching.getManagerName(),
                matching.getEmail(),
                matching.getContact(),
                matching.getPurpose(),
                matching.getDepartureDateTime(),
                matching.getDeparture(),
                matching.getDestination(),
                matching.getEstimatedPassengers(),
                matching.getVehicleType(),
                matching.getOperationType(),
                matching.getRequiredDocuments(),
                matching.getAdditionalRequests(),
                matching.getPrivacyConsent(),
                matching.getCreatedAt()
        );
    }
}
