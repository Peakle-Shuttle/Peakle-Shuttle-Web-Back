package com.peakle.shuttle.matching.repository;

import com.peakle.shuttle.global.enums.MatchingStatus;
import com.peakle.shuttle.matching.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    List<Matching> findAllByUser_UserCode(Long userCode);

    @Query("SELECT m FROM Matching m JOIN FETCH m.user WHERE m.status <> :status")
    List<Matching> findAllByStatusNot(@Param("status") MatchingStatus status);

    @Query("SELECT m FROM Matching m JOIN FETCH m.user WHERE m.matchingCode = :matchingCode")
    Optional<Matching> findByMatchingCodeWithUser(@Param("matchingCode") Long matchingCode);

    @Query("SELECT m.user.userCode, COUNT(m), " +
            "SUM(CASE WHEN m.status = com.peakle.shuttle.global.enums.MatchingStatus.COMPLETED THEN 1 ELSE 0 END) " +
            "FROM Matching m WHERE m.status <> com.peakle.shuttle.global.enums.MatchingStatus.DELETED " +
            "GROUP BY m.user.userCode")
    List<Object[]> countByUserGrouped();

    // 일별 매칭 수 집계 (DELETED 제외)
    @Query("SELECT CAST(m.createdAt AS LocalDate), COUNT(m) " +
           "FROM Matching m " +
           "WHERE m.status <> com.peakle.shuttle.global.enums.MatchingStatus.DELETED " +
           "AND CAST(m.createdAt AS LocalDate) BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(m.createdAt AS LocalDate) " +
           "ORDER BY CAST(m.createdAt AS LocalDate)")
    List<Object[]> findDailyMatchingCounts(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
