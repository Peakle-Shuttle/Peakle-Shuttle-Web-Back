package com.peakle.shuttle.open.repository;

import com.peakle.shuttle.open.entity.Open;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OpenRepository extends JpaRepository<Open, Long> {

    Optional<Open> findByOpenCode(Long openCode);

    @Query("SELECT o FROM Open o JOIN FETCH o.user WHERE o.user.userCode = :userCode")
    List<Open> findAllByUserCodeWithUser(Long userCode);

    @Query("SELECT o FROM Open o JOIN FETCH o.user")
    List<Open> findAllWithUser();

    Optional<Open> findByOpenCodeAndUserUserCode(Long openCode, Long userCode);
}
