package com.peakle.shuttle.qna.dto.request;

import jakarta.validation.constraints.NotBlank;

public record QnaCreateRequest(
        @NotBlank String qnaTitle,
        @NotBlank String qnaContent,
        Boolean qnaIsPrivate,
        String qnaImage
) {
}
