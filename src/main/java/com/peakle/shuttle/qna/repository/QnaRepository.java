package com.peakle.shuttle.qna.repository;

import com.peakle.shuttle.qna.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    @Query("SELECT q FROM Qna q JOIN FETCH q.user WHERE q.qnaCode = :qnaCode")
    Optional<Qna> findByQnaCode(@Param("qnaCode") Long qnaCode);

    @Query("SELECT q FROM Qna q JOIN FETCH q.user")
    List<Qna> findAllWithUser();

    @Query("SELECT q FROM Qna q JOIN FETCH q.user WHERE q.user.userCode = :userCode")
    List<Qna> findAllByUserCodeWithUser(Long userCode);

    Optional<Qna> findByQnaCodeAndUserUserCode(Long qnaCode, Long userCode);
}
