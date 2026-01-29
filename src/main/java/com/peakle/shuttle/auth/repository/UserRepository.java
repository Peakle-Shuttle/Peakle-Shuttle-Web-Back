package com.peakle.shuttle.auth.repository;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.global.enums.AuthProvider;
import com.peakle.shuttle.global.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** User 엔티티 데이터 접근 레포지토리 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
 * 주어진 userId와 상태에 일치하는 User를 조회합니다.
 *
 * @param userId 조회할 사용자의 식별자
 * @param status 조회 대상의 계정 상태
 * @return `userId`와 `status`에 일치하는 User를 포함하는 Optional, 일치하는 사용자가 없으면 비어있음
 */
Optional<User> findByUserIdAndStatus(String userId, Status status);

    /**
 * 특정 사용자 코드와 상태에 일치하는 사용자 정보를 조회한다.
 *
 * @param userCode 조회 대상 사용자의 고유 코드
 * @param status   조회 대상 사용자의 상태
 * @return 일치하는 User가 있으면 그 User를 담은 Optional, 없으면 빈 Optional
 */
Optional<User> findByUserCodeAndStatus(Long userCode, Status status);

    /**
 * 주어진 이메일과 상태에 일치하는 사용자 엔티티를 조회합니다.
 *
 * @param userEmail 조회할 사용자의 이메일
 * @param status 조회할 사용자의 상태
 * @return 지정한 이메일과 상태에 일치하는 User를 포함한 Optional, 없으면 비어있음
 */
Optional<User> findByUserEmailAndStatus(String userEmail, Status status);

    /**
 * 지정된 인증 제공자와 제공자 ID 및 계정 상태에 일치하는 사용자 엔티티를 조회합니다.
 *
 * @param provider   인증 제공자 (예: LOCAL, GOOGLE 등)
 * @param providerId 인증 제공자에서 발급한 고유 식별자
 * @param status     조회 대상 사용자의 상태
 * @return           조회된 User를 포함한 Optional, 존재하지 않으면 Optional.empty()
 */
Optional<User> findByProviderAndProviderIdAndStatus(AuthProvider provider, String providerId, Status status);

    /**
 * 지정한 userId와 상태(Status)를 가진 사용자가 데이터베이스에 존재하는지 확인한다.
 *
 * @param userId 확인할 사용자의 식별자
 * @param status 확인할 사용자 상태
 * @return `true`이면 해당 `userId`와 `status`를 동시에 만족하는 사용자가 존재하며, `false`이면 존재하지 않는다.
 */
boolean existsByUserIdAndStatus(String userId, Status status);

    /**
 * 주어진 이메일과 상태를 만족하는 사용자의 존재 여부를 확인한다.
 *
 * @param userEmail 검색할 사용자 이메일
 * @param status    검색에 적용할 사용자 상태
 * @return `true`이면 해당 이메일과 상태를 가진 사용자가 존재하고, `false`이면 존재하지 않는다.
 */
boolean existsByUserEmailAndStatus(String userEmail, Status status);
}