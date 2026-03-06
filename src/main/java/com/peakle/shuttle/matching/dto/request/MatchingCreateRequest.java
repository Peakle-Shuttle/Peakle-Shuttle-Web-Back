package com.peakle.shuttle.matching.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MatchingCreateRequest(
        @NotBlank String customerType,
        @NotBlank String organizationName,
        @NotBlank String managerName,
        @NotBlank String email,
        @NotBlank String contact,
        @NotBlank String purpose,
        @NotBlank String departureDateTime,
        String departure,
        String destination,
        @NotBlank String estimatedPassengers,
        @NotBlank String vehicleType,
        @NotBlank String operationType,
        String requiredDocuments,
        String additionalRequests,
        @NotBlank String privacyConsent
) {
}
