package com.peakle.shuttle.course.repository;

import com.peakle.shuttle.course.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

    Optional<Dispatch> findByDispatchCode(Long dispatchCode);

    List<Dispatch> findAllByCourseCourseCode(Long courseCode);
}
