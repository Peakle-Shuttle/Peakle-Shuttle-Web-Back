package com.peakle.shuttle.qna.repository;

import com.peakle.shuttle.qna.entity.QnaComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QnaCommentRepository extends JpaRepository<QnaComment, Long> {

    Optional<QnaComment> findByCommentCode(Long commentCode);

    List<QnaComment> findAllByQnaQnaCode(Long qnaCode);

    @Query("SELECT c FROM QnaComment c JOIN FETCH c.user WHERE c.qna.qnaCode = :qnaCode")
    List<QnaComment> findAllByQnaQnaCodeWithUser(@Param("qnaCode") Long qnaCode);
}
