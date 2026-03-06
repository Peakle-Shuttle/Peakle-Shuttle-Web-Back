package com.peakle.shuttle.qna.repository;

import com.peakle.shuttle.qna.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    @Query("SELECT q.user.userCode, COUNT(q), SUM(CASE WHEN q.qnaState = com.peakle.shuttle.global.enums.QnaStatus.ANSWERED THEN 1 ELSE 0 END) FROM Qna q GROUP BY q.user.userCode")
    List<Object[]> countByUserGrouped();

    // 일별 문의 수 집계
    @Query("SELECT CAST(q.createdAt AS LocalDate), COUNT(q) " +
           "FROM Qna q " +
           "WHERE CAST(q.createdAt AS LocalDate) BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(q.createdAt AS LocalDate) " +
           "ORDER BY CAST(q.createdAt AS LocalDate)")
    List<Object[]> findDailyInquiryCounts(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
