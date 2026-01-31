package com.peakle.shuttle.qna.repository;

import com.peakle.shuttle.qna.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    Optional<Qna> findByQnaId(String qnaId);
}
