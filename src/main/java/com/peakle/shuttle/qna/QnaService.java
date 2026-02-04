package com.peakle.shuttle.qna;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.qna.dto.QnaCreateRequest;
import com.peakle.shuttle.qna.dto.QnaListResponse;
import com.peakle.shuttle.qna.dto.QnaUpdateRequest;
import com.peakle.shuttle.qna.entity.Qna;
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
                .qnaState("PENDING")
                .qnaImage(request.qnaImage())
                .build();

        qnaRepository.save(qna);
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
