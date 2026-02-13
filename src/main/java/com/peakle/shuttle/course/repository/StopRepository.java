package com.peakle.shuttle.course.repository;

import com.peakle.shuttle.course.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StopRepository extends JpaRepository<Stop, Long> {

    Optional<Stop> findByStopCode(Long stopCode);
}
