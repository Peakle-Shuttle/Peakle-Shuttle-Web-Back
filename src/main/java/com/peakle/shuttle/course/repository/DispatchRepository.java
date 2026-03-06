package com.peakle.shuttle.course.repository;

import com.peakle.shuttle.course.entity.Dispatch;
import com.peakle.shuttle.global.enums.DispatchStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

    Optional<Dispatch> findByDispatchCode(Long dispatchCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Dispatch d JOIN FETCH d.course WHERE d.dispatchCode = :dispatchCode")
    Optional<Dispatch> findByDispatchCodeForUpdate(@Param("dispatchCode") Long dispatchCode);

    List<Dispatch> findAllByCourseCourseCode(Long courseCode);

    @Query("SELECT d FROM Dispatch d " +
            "JOIN FETCH d.course c " +
            "WHERE d.dispatchCode = :dispatchCode")
    Optional<Dispatch> findByDispatchCodeWithCourseAndStops(@Param("dispatchCode") Long dispatchCode);

    List<Dispatch> findAllByDispatchStatusAndDispatchDatetimeBefore(DispatchStatus status, LocalDateTime dateTime);
}
