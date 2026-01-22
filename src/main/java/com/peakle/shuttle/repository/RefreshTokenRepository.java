package com.peakle.shuttle.repository;

import com.peakle.shuttle.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserCode(Long userCode);

    void deleteByUserCode(Long userCode);

    void deleteByToken(String token);
}
