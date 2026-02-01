package com.peakle.shuttle.course.repository;

import com.peakle.shuttle.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(Long courseCode);

    // 특정 정차지점을 경유하는 노선 조회
    @Query("SELECT DISTINCT c FROM Course c " +
            "JOIN c.courseStops cs " +
            "WHERE cs.stop.stopCode = :stopCode")
    List<Course> findByStopId(@Param("stopCode") Long stopCode);

    // 노선과 정차지점 함께 조회 (N+1 방지)
    @Query("SELECT c FROM Course c " +
            "LEFT JOIN FETCH c.courseStops cs " +
            "LEFT JOIN FETCH cs.stop " +
            "WHERE c.courseCode = :courseCode")
    Optional<Course> findWithStopsByCourseId(@Param("courseCode") Long courseCode);
}
