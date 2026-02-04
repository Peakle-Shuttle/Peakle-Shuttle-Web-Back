package com.peakle.shuttle.admin.qna.dto;

import com.peakle.shuttle.qna.entity.Qna;

import java.time.LocalDateTime;

public record AdminQnaListResponse(
        Long qnaCode,
        String userName,
        String qnaTitle,
        LocalDateTime qnaDate,
        Boolean qnaIsPrivate,
        String qnaState
) {
    public static AdminQnaListResponse from(Qna qna) {
        return new AdminQnaListResponse(
                qna.getQnaCode(),
                qna.getUser().getUserName(),
                qna.getQnaTitle(),
                qna.getQnaDate(),
                qna.getQnaIsPrivate(),
                qna.getQnaState()
        );
    }
}
