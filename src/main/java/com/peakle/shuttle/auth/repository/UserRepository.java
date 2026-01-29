package com.peakle.shuttle.auth.repository;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.global.enums.AuthProvider;
import com.peakle.shuttle.global.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserIdAndStatus(String userId, Status status);

    Optional<User> findByUserCodeAndStatus(Long userCode, Status status);

    Optional<User> findByUserEmailAndStatus(String userEmail, Status status);

    Optional<User> findByProviderAndProviderIdAndStatus(AuthProvider provider, String providerId, Status status);

    boolean existsByUserIdAndStatus(String userId, Status status);

    boolean existsByUserEmailAndStatus(String userEmail, Status status);
}
