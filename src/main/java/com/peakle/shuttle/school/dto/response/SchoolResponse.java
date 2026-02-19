package com.peakle.shuttle.school.dto.response;

import com.peakle.shuttle.school.entity.School;

import java.time.LocalDateTime;

public record SchoolResponse(
    Long schoolCode,
    String schoolName,
    String schoolAddress,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static SchoolResponse from(School school) {
        return new SchoolResponse(
                school.getSchoolCode(),
                school.getSchoolName(),
                school.getSchoolAddress(),
                school.getCreatedAt(),
                school.getUpdatedAt()
        );
    }
}
