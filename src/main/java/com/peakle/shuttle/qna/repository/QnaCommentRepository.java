package com.peakle.shuttle.qna.repository;

import com.peakle.shuttle.qna.entity.QnaComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QnaCommentRepository extends JpaRepository<QnaComment, Long> {

    Optional<QnaComment> findByCommentCode(Long commentCode);

    List<QnaComment> findAllByQnaQnaCode(Long qnaCode);
}
