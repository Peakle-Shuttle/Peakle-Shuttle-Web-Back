package com.peakle.shuttle.qna.dto.response;

import com.peakle.shuttle.qna.entity.Qna;

import java.time.LocalDateTime;

public record QnaListResponse(
        Long qnaCode,
        String qnaTitle,
        LocalDateTime qnaDate,
        String qnaState,
        Boolean qnaIsPrivate,
        String qnaContent,
        String qnaImage
) {
    public static QnaListResponse from(Qna qna) {
        return new QnaListResponse(
                qna.getQnaCode(),
                qna.getQnaTitle(),
                qna.getQnaDate(),
                qna.getQnaState(),
                qna.getQnaIsPrivate(),
                qna.getQnaContent(),
                qna.getQnaImage()
        );
    }
}
