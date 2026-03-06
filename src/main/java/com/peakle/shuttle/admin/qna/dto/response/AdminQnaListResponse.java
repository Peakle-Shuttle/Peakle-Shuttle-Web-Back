package com.peakle.shuttle.admin.qna.dto.response;

import com.peakle.shuttle.global.enums.QnaStatus;
import com.peakle.shuttle.qna.entity.Qna;

import java.time.LocalDateTime;

public record AdminQnaListResponse(
        Long qnaCode,
        Long userCode,
        String userName,
        String qnaTitle,
        LocalDateTime qnaDate,
        Boolean qnaIsPrivate,
        QnaStatus qnaState
) {
    public static AdminQnaListResponse from(Qna qna) {
        return new AdminQnaListResponse(
                qna.getQnaCode(),
                qna.getUser().getUserCode(),
                qna.getUser().getUserName(),
                qna.getQnaTitle(),
                qna.getQnaDate(),
                qna.getQnaIsPrivate(),
                qna.getQnaState()
        );
    }
}
