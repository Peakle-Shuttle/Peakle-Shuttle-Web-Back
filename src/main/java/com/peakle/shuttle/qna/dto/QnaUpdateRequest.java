package com.peakle.shuttle.qna.dto;

import jakarta.validation.constraints.NotNull;

public record QnaUpdateRequest(
        @NotNull Long qnaCode,
        String qnaTitle,
        String qnaContent,
        Boolean qnaIsPrivate,
        String qnaImage
) {
}
