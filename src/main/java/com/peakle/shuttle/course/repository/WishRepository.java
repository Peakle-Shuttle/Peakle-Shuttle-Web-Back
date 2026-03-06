package com.peakle.shuttle.course.repository;

import com.peakle.shuttle.course.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByCourseCourseCodeAndUserUserCode(Long courseCode, Long userCode);

    boolean existsByCourseCourseCodeAndUserUserCode(Long courseCode, Long userCode);

    @Query("SELECT w.course.courseCode FROM Wish w WHERE w.course.courseCode IN :courseCodes AND w.user.userCode = :userCode")
    List<Long> findWishedCourseCodes(@Param("courseCodes") List<Long> courseCodes, @Param("userCode") Long userCode);
}
