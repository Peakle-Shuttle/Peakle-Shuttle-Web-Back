package com.peakle.shuttle.school.repository;

import com.peakle.shuttle.school.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {

    Optional<School> findBySchoolCode(Long schoolCode);
}
