package com.peakle.shuttle.admin.stats.dto.response;

public record StatsDetailResponse(
        String date,
        long orders,
        long revenue,
        long visits,
        long signups,
        long inquiries,
        long matchings
) {}
