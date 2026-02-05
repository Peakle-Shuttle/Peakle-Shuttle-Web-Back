package com.peakle.shuttle.auth.repository;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.global.enums.AuthProvider;
import com.peakle.shuttle.global.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** User 엔티티 데이터 접근 레포지토리 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserIdAndStatus(String userId, UserStatus userStatus);

    Optional<User> findByUserCodeAndStatus(Long userCode, UserStatus userStatus);

    Optional<User> findByUserEmailAndStatus(String userEmail, UserStatus userStatus);

    Optional<User> findByProviderAndProviderIdAndStatus(AuthProvider provider, String providerId, UserStatus userStatus);

    boolean existsByUserIdAndStatus(String userId, UserStatus userStatus);

    boolean existsByUserEmailAndStatus(String userEmail, UserStatus userStatus);

    List<User> findAllByStatus(UserStatus userStatus);

    Optional<User> findByUserCode(Long userCode);
}
