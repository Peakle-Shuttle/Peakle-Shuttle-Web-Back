package com.peakle.shuttle.qna.dto.response;

import com.peakle.shuttle.global.enums.QnaStatus;
import com.peakle.shuttle.qna.entity.Qna;

import java.time.LocalDateTime;
import java.util.List;

public record QnaDetailResponse(
        Long qnaCode,
        String qnaTitle,
        String qnaContent,
        String qnaImage,
        LocalDateTime qnaDate,
        Boolean qnaIsPrivate,
        QnaStatus qnaState,
        List<QnaCommentResponse> comments
) {
    public static QnaDetailResponse of(Qna qna, List<QnaCommentResponse> comments) {
        return new QnaDetailResponse(
                qna.getQnaCode(),
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
