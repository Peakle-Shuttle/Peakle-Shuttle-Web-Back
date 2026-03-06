package com.peakle.shuttle.admin.stats.dto.response;

public record PassengerStatsResponse(
        String date,
        Long passengerCount
) {
    public static PassengerStatsResponse of(String date, Long count) {
        return new PassengerStatsResponse(date, count);
    }
}
