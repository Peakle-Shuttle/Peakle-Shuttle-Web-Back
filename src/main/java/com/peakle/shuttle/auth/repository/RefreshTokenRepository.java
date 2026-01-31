package com.peakle.shuttle.auth.repository;

import com.peakle.shuttle.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Refresh Token 엔티티 데이터 접근 레포지토리 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserCode(Long userCode);

    void deleteByUserCode(Long userCode);

    void deleteByToken(String token);
}
