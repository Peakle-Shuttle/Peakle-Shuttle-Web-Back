package com.peakle.shuttle.auth.domain;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.provider.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    Optional<User> findByUserEmail(String userEmail);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    boolean existsByUserId(String userId);

    boolean existsByUserEmail(String userEmail);
}
