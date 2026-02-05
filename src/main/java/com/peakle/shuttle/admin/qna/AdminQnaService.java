package com.peakle.shuttle.admin.qna;

import com.peakle.shuttle.admin.qna.dto.request.QnaCommentCreateRequest;
import com.peakle.shuttle.admin.qna.dto.response.AdminQnaCommentResponse;
import com.peakle.shuttle.admin.qna.dto.response.AdminQnaDetailResponse;
import com.peakle.shuttle.admin.qna.dto.response.AdminQnaListResponse;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.global.enums.UserStatus;
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

    /**
     * 전체 1:1 문의 목록을 조회합니다.
     *
     * @return 문의 목록
     */
    public List<AdminQnaListResponse> getQnaList() {
        return qnaRepository.findAllWithUser().stream()
                .map(AdminQnaListResponse::from)
                .toList();
    }

    /**
     * 1:1 문의 상세 내용과 답변을 조회합니다.
     *
     * @param qnaCode 문의 코드
     * @return 문의 상세 정보
     * @throws AuthException 문의를 찾을 수 없는 경우
     */
    public AdminQnaDetailResponse getQnaDetail(Long qnaCode) {
        Qna qna = qnaRepository.findByQnaCode(qnaCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_QNA));

        List<AdminQnaCommentResponse> comments = qnaCommentRepository.findAllByQnaQnaCode(qnaCode).stream()
                .map(AdminQnaCommentResponse::from)
                .toList();

        return AdminQnaDetailResponse.of(qna, comments);
    }

    /**
     * 1:1 문의에 관리자 답변을 등록합니다.
     *
     * @param adminUserCode 관리자 사용자 코드
     * @param request 답변 생성 요청 정보
     * @return 등록된 답변 정보
     * @throws AuthException 문의 또는 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public AdminQnaCommentResponse createComment(Long adminUserCode, QnaCommentCreateRequest request) {
        Qna qna = qnaRepository.findByQnaCode(request.qnaCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_QNA));

        var admin = userRepository.findByUserCodeAndUserStatus(adminUserCode, UserStatus.ACTIVE)
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
