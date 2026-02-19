package com.peakle.shuttle.admin.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QnaCommentCreateRequest(
        @NotNull Long qnaCode,
        @NotBlank String commentContent,
        String commentImage
) {
}
