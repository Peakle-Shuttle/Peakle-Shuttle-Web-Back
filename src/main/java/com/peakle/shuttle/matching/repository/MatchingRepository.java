package com.peakle.shuttle.matching.repository;

import com.peakle.shuttle.matching.entity.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Long> {

    List<Matching> findAllByUser_UserCode(Long userCode);
}
