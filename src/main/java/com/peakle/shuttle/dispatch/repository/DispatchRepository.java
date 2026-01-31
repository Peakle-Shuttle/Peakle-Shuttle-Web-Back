package com.peakle.shuttle.dispatch.repository;

import com.peakle.shuttle.dispatch.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

    Optional<Dispatch> findByDispatchId(String dispatchId);
}
