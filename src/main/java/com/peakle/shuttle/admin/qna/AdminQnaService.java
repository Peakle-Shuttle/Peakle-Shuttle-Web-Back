package com.peakle.shuttle.admin.qna;

import com.peakle.shuttle.admin.qna.dto.AdminQnaCommentResponse;
import com.peakle.shuttle.admin.qna.dto.AdminQnaDetailResponse;
import com.peakle.shuttle.admin.qna.dto.AdminQnaListResponse;
import com.peakle.shuttle.admin.qna.dto.QnaCommentCreateRequest;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.global.enums.Status;
import com.peakle.shuttle.qna.entity.Qna;
import com.peakle.shuttle.qna.entity.QnaComment;
import com.peakle.shuttle.qna.repository.QnaCommentRepository;
import com.peakle.shuttle.qna.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminQnaService {

    private final QnaRepository qnaRepository;
    private final QnaCommentRepository qnaCommentRepository;
    private final UserRepository userRepository;

    public List<AdminQnaListResponse> getQnaList() {
        return qnaRepository.findAllWithUser().stream()
                .map(AdminQnaListResponse::from)
                .toList();
    }

    public AdminQnaDetailResponse getQnaDetail(Long qnaCode) {
        Qna qna = qnaRepository.findByQnaCode(qnaCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_QNA));

        List<AdminQnaCommentResponse> comments = qnaCommentRepository.findAllByQnaQnaCode(qnaCode).stream()
                .map(AdminQnaCommentResponse::from)
                .toList();

        return AdminQnaDetailResponse.of(qna, comments);
    }

    @Transactional
    public AdminQnaCommentResponse createComment(Long adminUserCode, QnaCommentCreateRequest request) {
        Qna qna = qnaRepository.findByQnaCode(request.qnaCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_QNA));

        var admin = userRepository.findByUserCodeAndStatus(adminUserCode, Status.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        QnaComment comment = QnaComment.builder()
                .qna(qna)
                .adminId(admin.getUserId())
                .commentDate(LocalDateTime.now())
                .commentContent(request.commentContent())
                .commentImage(request.commentImage())
                .build();

        qnaCommentRepository.save(comment);
        qna.updateQnaState("ANSWERED");

        return AdminQnaCommentResponse.from(comment);
    }
}
