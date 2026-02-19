package com.peakle.shuttle.admin.utm.repository;

import com.peakle.shuttle.admin.utm.entity.UtmStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtmStatRepository extends JpaRepository<UtmStat, Long> {

    Optional<UtmStat> findByUtmSourceAndUtmMediumAndUtmCampaign(
            String utmSource,
            String utmMedium,
            String utmCampaign
    );

    List<UtmStat> findAllByOrderByUtmCountDesc();

    @Query("SELECT u FROM UtmStat u WHERE u.utmSource = :source ORDER BY u.utmCount DESC")
    List<UtmStat> findByUtmSourceOrderByUtmCountDesc(@Param("source") String source);
}
