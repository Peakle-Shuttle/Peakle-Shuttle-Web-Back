package com.peakle.shuttle.course.repository;

import com.peakle.shuttle.course.entity.Course;
import com.peakle.shuttle.global.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCourseCode(Long courseCode);

    // 전체 노선 조회 (관리자용 - 상태 무관)
    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.dispatches")
    List<Course> findAllWithDispatchesAndStops();

    // 상태별 노선 조회 (사용자용)
    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.dispatches WHERE c.courseStatus = :status")
    List<Course> findAllWithDispatchesByStatus(@Param("status") CourseStatus status);

    // 학교별 노선 조회
    List<Course> findBySchoolSchoolCode(Long schoolCode);

    // 학교별 노선과 배차 함께 조회 (N+1 방지, 관리자용)
    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.dispatches " +
            "WHERE c.school.schoolCode = :schoolCode")
    List<Course> findAllBySchoolCodeWithDispatchesAndStops(@Param("schoolCode") Long schoolCode);

    // 학교별 + 상태별 노선과 배차 함께 조회 (사용자용)
    @Query("SELECT DISTINCT c FROM Course c " +
            "LEFT JOIN FETCH c.dispatches " +
            "WHERE c.school.schoolCode = :schoolCode AND c.courseStatus = :status")
    List<Course> findAllBySchoolCodeAndStatusWithDispatches(@Param("schoolCode") Long schoolCode, @Param("status") CourseStatus status);
}
