package com.peakle.shuttle.wish.repository;

import com.peakle.shuttle.wish.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByCourseCourseCodeAndUserUserCode(Long courseCode, Long userCode);

    boolean existsByCourseCourseCodeAndUserUserCode(Long courseCode, Long userCode);
}
