package com.peakle.shuttle.qna;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.qna.dto.request.QnaCommentCreateRequest;
import com.peakle.shuttle.qna.dto.request.QnaCreateRequest;
import com.peakle.shuttle.qna.dto.request.QnaUpdateRequest;
import com.peakle.shuttle.qna.dto.response.QnaCommentResponse;
import com.peakle.shuttle.qna.dto.response.QnaDetailResponse;
import com.peakle.shuttle.qna.dto.response.QnaListResponse;
import com.peakle.shuttle.global.enums.QnaStatus;
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
public class QnaService {

    private final QnaRepository qnaRepository;
    private final QnaCommentRepository qnaCommentRepository;
    private final UserRepository userRepository;

    /**
     * 내 1:1 문의 목록을 조회합니다.
     *
     * @param userCode 사용자 코드
     * @return 문의 목록
     */
    public List<QnaListResponse> getMyQnas(Long userCode) {
        return qnaRepository.findAllByUserCodeWithUser(userCode).stream()
                .map(QnaListResponse::from)
                .toList();
    }

    /**
     * 1:1 문의 상세 내용과 답변을 조회합니다.
     * 비공개 문의인 경우 본인만 조회할 수 있습니다.
     *
     * @param userCode 사용자 코드
     * @param qnaCode 문의 코드
     * @return 문의 상세 정보
     * @throws AuthException 문의를 찾을 수 없거나 권한이 없는 경우
     */
    public QnaDetailResponse getQnaDetail(Long userCode, Long qnaCode) {
        Qna qna = qnaRepository.findByQnaCode(qnaCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_QNA));

        if (qna.getQnaIsPrivate() && !qna.getUser().getUserCode().equals(userCode)) {
            throw new AuthException(ExceptionCode.NOT_AUTHORIZED);
        }

        List<QnaCommentResponse> comments = qnaCommentRepository.findAllByQnaQnaCodeWithUser(qnaCode).stream()
                .map(QnaCommentResponse::from)
                .toList();

        return QnaDetailResponse.of(qna, comments);
    }

    /**
     * 1:1 문의를 작성합니다.
     *
     * @param userCode 사용자 코드
     * @param request 문의 작성 요청 정보
     * @throws AuthException 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public void createQna(Long userCode, QnaCreateRequest request) {
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        Qna qna = Qna.builder()
                .user(user)
                .qnaTitle(request.qnaTitle())
                .qnaContent(request.qnaContent())
                .qnaDate(LocalDateTime.now())
                .qnaIsPrivate(request.qnaIsPrivate() != null ? request.qnaIsPrivate() : false)
                .qnaState(QnaStatus.PENDING)
                .qnaImage(request.qnaImage())
                .build();

        qnaRepository.save(qna);
    }

    /**
     * 1:1 문의에 사용자 재문의를 등록합니다.
     *
     * @param userCode 사용자 코드
     * @param request 재문의 작성 요청 정보
     * @throws AuthException 문의 또는 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public void createComment(Long userCode, QnaCommentCreateRequest request) {
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        Qna qna = qnaRepository.findByQnaCodeAndUserUserCode(request.qnaCode(), userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_QNA));

        QnaComment comment = QnaComment.builder()
                .qna(qna)
                .user(user)
                .commentDate(LocalDateTime.now())
                .commentContent(request.commentContent())
                .commentImage(request.commentImage())
                .build();

        qnaCommentRepository.save(comment);
        qna.updateQnaState(QnaStatus.PENDING);
    }

    /**
     * 1:1 문의를 수정합니다. null이 아닌 필드만 업데이트됩니다.
     *
     * @param userCode 사용자 코드
     * @param request 문의 수정 요청 정보
     * @throws AuthException 문의를 찾을 수 없는 경우
     */
    @Transactional
    public void updateQna(Long userCode, QnaUpdateRequest request) {
        Qna qna = qnaRepository.findByQnaCodeAndUserUserCode(request.qnaCode(), userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_QNA));

        if (request.qnaTitle() != null) qna.updateQnaTitle(request.qnaTitle());
        if (request.qnaContent() != null) qna.updateQnaContent(request.qnaContent());
        if (request.qnaIsPrivate() != null) qna.updateQnaIsPrivate(request.qnaIsPrivate());
        if (request.qnaImage() != null) qna.updateQnaImage(request.qnaImage());
    }

    /**
     * 1:1 문의를 삭제합니다.
     *
     * @param userCode 사용자 코드
     * @param qnaCode 문의 코드
     * @throws AuthException 문의를 찾을 수 없는 경우
     */
    @Transactional
    public void deleteQna(Long userCode, Long qnaCode) {
        Qna qna = qnaRepository.findByQnaCodeAndUserUserCode(qnaCode, userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_QNA));
        qnaRepository.delete(qna);
    }
}
