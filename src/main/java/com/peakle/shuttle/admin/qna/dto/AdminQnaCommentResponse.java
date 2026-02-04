package com.peakle.shuttle.admin.qna.dto;

import com.peakle.shuttle.qna.entity.QnaComment;

import java.time.LocalDateTime;

public record AdminQnaCommentResponse(
        Long commentCode,
        String adminId,
        String commentContent,
        String commentImage,
        LocalDateTime commentDate
) {
    public static AdminQnaCommentResponse from(QnaComment comment) {
        return new AdminQnaCommentResponse(
                comment.getCommentCode(),
                comment.getAdminId(),
                comment.getCommentContent(),
                comment.getCommentImage(),
                comment.getCommentDate()
        );
    }
}
