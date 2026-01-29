package com.peakle.shuttle.auth.repository;

import com.peakle.shuttle.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Refresh Token 엔티티 데이터 접근 레포지토리 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
 * 주어진 토큰 값으로 RefreshToken 엔티티를 조회한다.
 *
 * @param token 조회할 리프레시 토큰 문자열
 * @return `token`에 해당하는 RefreshToken을 포함한 Optional, 존재하지 않으면 Optional.empty()
 */
Optional<RefreshToken> findByToken(String token);

    /**
 * 주어진 사용자 코드로 RefreshToken을 조회합니다.
 *
 * @param userCode 조회할 사용자의 고유 코드(식별자)
 * @return 주어진 사용자 코드에 연결된 RefreshToken을 포함한 Optional, 존재하지 않으면 비어 있음
 */
Optional<RefreshToken> findByUserCode(Long userCode);

    /**
 * 지정된 사용자 코드와 연관된 RefreshToken 엔티티를 삭제한다.
 *
 * @param userCode 삭제할 RefreshToken에 연관된 사용자 코드
 */
void deleteByUserCode(Long userCode);

    /**
 * 주어진 토큰 값에 해당하는 RefreshToken 엔티티를 삭제합니다.
 *
 * @param token 삭제할 리프레시 토큰의 값
 */
void deleteByToken(String token);
}