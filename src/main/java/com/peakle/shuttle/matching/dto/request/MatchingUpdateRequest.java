package com.peakle.shuttle.matching.dto.request;

public record MatchingUpdateRequest(
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
        String privacyConsent
) {
}
