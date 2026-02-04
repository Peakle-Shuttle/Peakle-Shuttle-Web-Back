package com.peakle.shuttle.admin.qna.dto;

import com.peakle.shuttle.qna.entity.Qna;

import java.time.LocalDateTime;
import java.util.List;

public record AdminQnaDetailResponse(
        Long qnaCode,
        String userName,
        String qnaTitle,
        String qnaContent,
        String qnaImage,
        LocalDateTime qnaDate,
        Boolean qnaIsPrivate,
        String qnaState,
        List<AdminQnaCommentResponse> comments
) {
    public static AdminQnaDetailResponse of(Qna qna, List<AdminQnaCommentResponse> comments) {
        return new AdminQnaDetailResponse(
                qna.getQnaCode(),
                qna.getUser().getUserName(),
                qna.getQnaTitle(),
                qna.getQnaContent(),
                qna.getQnaImage(),
                qna.getQnaDate(),
                qna.getQnaIsPrivate(),
                qna.getQnaState(),
                comments
        );
    }
}
