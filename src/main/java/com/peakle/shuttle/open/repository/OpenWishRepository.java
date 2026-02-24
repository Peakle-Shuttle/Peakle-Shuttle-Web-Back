package com.peakle.shuttle.open.repository;

import com.peakle.shuttle.open.entity.OpenWish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OpenWishRepository extends JpaRepository<OpenWish, Long> {

    Optional<OpenWish> findByOpenOpenCodeAndUserUserCode(Long openCode, Long userCode);

    boolean existsByOpenOpenCodeAndUserUserCode(Long openCode, Long userCode);

    @Query("SELECT ow.open.openCode, COUNT(ow) FROM OpenWish ow WHERE ow.open.openCode IN :openCodes GROUP BY ow.open.openCode")
    List<Object[]> countByOpenCodes(@Param("openCodes") List<Long> openCodes);

    @Query("SELECT ow.open.openCode FROM OpenWish ow WHERE ow.open.openCode IN :openCodes AND ow.user.userCode = :userCode")
    List<Long> findWishedOpenCodes(@Param("openCodes") List<Long> openCodes, @Param("userCode") Long userCode);
}
