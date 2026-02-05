package com.peakle.shuttle.open.repository;

import com.peakle.shuttle.open.entity.OpenWish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpenWishRepository extends JpaRepository<OpenWish, Long> {

    Optional<OpenWish> findByOpenOpenCodeAndUserUserCode(Long openCode, Long userCode);

    boolean existsByOpenOpenCodeAndUserUserCode(Long openCode, Long userCode);
}
