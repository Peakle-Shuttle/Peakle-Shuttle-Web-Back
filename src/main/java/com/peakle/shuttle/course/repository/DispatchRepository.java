package com.peakle.shuttle.course.repository;

import com.peakle.shuttle.course.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

    Optional<Dispatch> findByDispatchCode(Long dispatchCode);

    List<Dispatch> findAllByCourseCourseCode(Long courseCode);

    @Query("SELECT d FROM Dispatch d " +
            "JOIN FETCH d.course c " +
            "LEFT JOIN FETCH c.courseStops cs " +
            "LEFT JOIN FETCH cs.stop " +
            "WHERE d.dispatchCode = :dispatchCode")
    Optional<Dispatch> findByDispatchCodeWithCourseAndStops(@Param("dispatchCode") Long dispatchCode);
}
