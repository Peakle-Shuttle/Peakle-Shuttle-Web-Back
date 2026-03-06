package com.peakle.shuttle.admin.matching.dto.request;

import com.peakle.shuttle.global.enums.MatchingStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record AdminMatchingStatusUpdateRequest(
        @NotNull MatchingStatus status
) {
    @AssertTrue(message = "DELETED 상태로는 변경할 수 없습니다. DELETE API를 사용해주세요.")
    private boolean isValidStatus() {
        return status != MatchingStatus.DELETED && status != MatchingStatus.WAITING;
    }
}
