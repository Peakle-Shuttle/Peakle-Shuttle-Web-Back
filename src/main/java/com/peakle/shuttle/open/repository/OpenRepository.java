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

    @Query("SELECT o FROM Open o JOIN FETCH o.user WHERE o.openCode = :openCode")
    Optional<Open> findByOpenCodeWithUser(Long openCode);

    @Query("SELECT o.user.userCode, COUNT(o), SUM(CASE WHEN o.openStatus = com.peakle.shuttle.global.enums.OpenStatus.COMPLETED THEN 1 ELSE 0 END) FROM Open o GROUP BY o.user.userCode")
    List<Object[]> countByUserGrouped();
}
