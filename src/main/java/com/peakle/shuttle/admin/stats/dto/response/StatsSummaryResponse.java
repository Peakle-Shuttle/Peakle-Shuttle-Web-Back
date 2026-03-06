package com.peakle.shuttle.admin.stats.dto.response;

import java.util.List;

public record StatsSummaryResponse(
        List<DailyStatEntry> dailyStats,
        StatEntry selectedPeriodTotal
) {

    public record DailyStatEntry(
            String date,
            long orders,
            long revenue,
            long visits,
            long signups,
            long inquiries,
            long matchings
    ) {}

    public record StatEntry(
            long orders,
            long revenue,
            long visits,
            long signups,
            long inquiries,
            long matchings
    ) {}
}
