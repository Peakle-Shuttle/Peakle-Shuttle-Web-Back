package com.peakle.shuttle.qna.dto.response;

import com.peakle.shuttle.qna.entity.QnaComment;

import java.time.LocalDateTime;

public record QnaCommentResponse(
        Long commentCode,
        String adminId,
        String commentContent,
        String commentImage,
        LocalDateTime commentDate
) {
    public static QnaCommentResponse from(QnaComment comment) {
        return new QnaCommentResponse(
                comment.getCommentCode(),
                comment.getAdminId(),
                comment.getCommentContent(),
                comment.getCommentImage(),
                comment.getCommentDate()
        );
    }
}
