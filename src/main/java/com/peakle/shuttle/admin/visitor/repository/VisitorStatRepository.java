package com.peakle.shuttle.admin.visitor.repository;

import com.peakle.shuttle.admin.visitor.entity.VisitorStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VisitorStatRepository extends JpaRepository<VisitorStat, Long> {

    Optional<VisitorStat> findByStatDate(LocalDate statDate);

    List<VisitorStat> findByStatDateBetweenOrderByStatDateDesc(LocalDate startDate, LocalDate endDate);
}
