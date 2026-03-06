package com.peakle.shuttle.auth.repository;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.global.enums.AuthProvider;
import com.peakle.shuttle.global.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** User 엔티티 데이터 접근 레포지토리 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserIdAndUserStatus(String userId, UserStatus userStatus);

    Optional<User> findByUserCodeAndUserStatus(Long userCode, UserStatus userStatus);

    Optional<User> findByUserEmailAndUserStatus(String userEmail, UserStatus userStatus);

    Optional<User> findByProviderAndProviderIdAndUserStatus(AuthProvider provider, String providerId, UserStatus userStatus);

    boolean existsByUserIdAndUserStatus(String userId, UserStatus userStatus);

    boolean existsByUserEmailAndUserStatus(String userEmail, UserStatus userStatus);

    boolean existsByProviderAndProviderIdAndUserStatus(AuthProvider provider, String providerId, UserStatus userStatus);

    List<User> findAllByUserStatus(UserStatus userStatus);

    Optional<User> findByUserCode(Long userCode);

    List<User> findBySchoolSchoolCodeAndUserStatus(Long schoolCode, UserStatus userStatus);

    Optional<User> findByUserNameAndUserNumberAndUserEmailAndUserStatus(String userName, String userNumber, String userEmail, UserStatus userStatus);

    Optional<User> findByUserNameAndUserNumberAndUserIdAndUserEmailAndUserStatus(String userName, String userNumber, String userId, String userEmail, UserStatus userStatus);

    // 일별 가입자 수 집계
    @Query("SELECT CAST(u.createdAt AS LocalDate), COUNT(u) " +
           "FROM User u " +
           "WHERE u.userStatus = 'ACTIVE' " +
           "AND CAST(u.createdAt AS LocalDate) BETWEEN :startDate AND :endDate " +
           "GROUP BY CAST(u.createdAt AS LocalDate) " +
           "ORDER BY CAST(u.createdAt AS LocalDate)")
    List<Object[]> findDailySignupCounts(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
