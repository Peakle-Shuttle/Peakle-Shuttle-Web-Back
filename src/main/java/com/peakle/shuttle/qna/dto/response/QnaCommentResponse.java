package com.peakle.shuttle.qna.dto.response;

import com.peakle.shuttle.global.enums.Role;
import com.peakle.shuttle.qna.entity.QnaComment;

import java.time.LocalDateTime;

public record QnaCommentResponse(
        Long commentCode,
        Long userCode,
        String userName,
        Role userRole,
        String commentContent,
        String commentImage,
        LocalDateTime commentDate
) {
    public static QnaCommentResponse from(QnaComment comment) {
        return new QnaCommentResponse(
                comment.getCommentCode(),
                comment.getUser().getUserCode(),
                comment.getUser().getUserName(),
                comment.getUser().getUserRole(),
                comment.getCommentContent(),
                comment.getCommentImage(),
                comment.getCommentDate()
        );
    }
}
