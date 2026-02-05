package com.peakle.shuttle.auth.service;

import com.peakle.shuttle.auth.dto.request.UserEmailRequest;
import com.peakle.shuttle.auth.dto.request.UserIdRequest;
import com.peakle.shuttle.auth.dto.request.UserInfoRequest;
import com.peakle.shuttle.auth.dto.request.UserPwRequest;
import com.peakle.shuttle.auth.dto.response.UserClientResponse;
import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.global.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/** 회원 정보 조회, 수정, 삭제 비즈니스 로직을 처리하는 서비스 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 코드로 회원 정보를 조회합니다.
     *
     * @param code 사용자 고유 코드
     * @return 사용자 상세 정보
     * @throws AuthException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public UserClientResponse getInfo(@NotNull Long code) {
        final User user = userRepository.findByUserCodeAndStatus(code, UserStatus.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        return UserClientResponse.builder()
                .userCode(user.getUserCode())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userRole(user.getUserRole())
                .userGender(user.getUserGender())
                .userNumber(user.getUserNumber())
                .userBirth(user.getUserBirth())
                .userSchool(user.getUserSchool())
                .userMajor(user.getUserMajor())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 활성 사용자 ID 존재 여부를 확인합니다.
     *
     * @param userId 확인할 사용자 ID
     * @return 해당 ID의 활성 사용자 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByUserId(@NotNull String userId) {
        return userRepository.existsByUserIdAndStatus(userId, UserStatus.ACTIVE);
    }

    /**
     * 이메일로 사용자 ID를 조회합니다.
     *
     * @param userEmail 조회할 이메일
     * @return 해당 이메일로 등록된 사용자 ID
     * @throws AuthException 사용자를 찾을 수 없는 경우
     * @throws AuthException 카카오 가입을 활용한 경우
     */
    @Transactional(readOnly = true)
    public String findUserIdByEmail(@NotNull String userEmail) {
        final User user = userRepository.findByUserEmailAndStatus(userEmail, UserStatus.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        if (user.getUserId().startsWith("kakao_")) {
            throw new AuthException(ExceptionCode.ANOTHER_PROVIDER);
        }

        return user.getUserId();
    }

    /**
     * 사용자 ID를 변경합니다.
     *
     * @param code 사용자 고유 코드
     * @param request 변경할 ID 정보
     * @throws AuthException 사용자가 없거나 ID가 중복인 경우
     */
    @Transactional
    public void changeId(@NotNull Long code, @NotNull UserIdRequest request) {
        final User user = userRepository.findByUserCodeAndStatus(code, UserStatus.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        if (user.getUserId().equals(request.userId()) ||
            userRepository.existsByUserIdAndStatus(request.userId(), UserStatus.ACTIVE)) {
            throw new AuthException(ExceptionCode.DUPLICATE_ID);
        }

        user.updateUserId(request.userId());
    }

    /**
     * 사용자 이메일을 변경합니다.
     *
     * @param code 사용자 고유 코드
     * @param request 변경할 이메일 정보
     * @throws AuthException 사용자가 없거나 이메일이 중복인 경우
     */
    @Transactional
    public void changeEmail(@NotNull Long code, @NotNull UserEmailRequest request) {
        final User user = userRepository.findByUserCodeAndStatus(code, UserStatus.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        if (Objects.equals(user.getUserEmail(), request.userEmail()) ||
            userRepository.existsByUserEmailAndStatus(request.userEmail(), UserStatus.ACTIVE)) {
            throw new AuthException(ExceptionCode.DUPLICATE_EMAIL);
        }

        user.updateUserEmail(request.userEmail());
    }

    /**
     * 비밀번호를 변경합니다. 기존 비밀번호 일치 여부를 먼저 검증합니다.
     *
     * @param code 사용자 고유 코드
     * @param request 기존/새 비밀번호 정보
     * @throws AuthException 사용자가 없거나 기존 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    public void changePassword(@NotNull Long code, @NotNull UserPwRequest request) {
        final User user = userRepository.findByUserCodeAndStatus(code, UserStatus.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.oldPassword(), user.getUserPassword())) {
            throw new AuthException(ExceptionCode.INVALID_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    /**
     * 사용자 정보를 부분 수정합니다. null이 아닌 필드만 업데이트됩니다.
     *
     * @param code 사용자 고유 코드
     * @param userInfoRequest 수정할 사용자 정보 (전화번호, 학교, 전공, 생년월일)
     * @throws AuthException 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public void updateInfo(@NotNull Long code, UserInfoRequest userInfoRequest) {
        final User user = userRepository.findByUserCodeAndStatus(code, UserStatus.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        if (userInfoRequest.userNumber() != null) user.updateUserNumber(userInfoRequest.userNumber());
        if (userInfoRequest.userSchool() != null) user.updateUserSchool(userInfoRequest.userSchool());
        if (userInfoRequest.userMajor() != null) user.updateUserMajor(userInfoRequest.userMajor());
        if (userInfoRequest.userBirth() != null) user.updateUserBirth(userInfoRequest.userBirth());
    }

    /**
     * 회원 탈퇴를 처리합니다 (소프트 삭제).
     *
     * @param code 사용자 고유 코드
     * @throws AuthException 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public void removeUser(@NotNull Long code) {
        final User user = userRepository.findByUserCodeAndStatus(code, UserStatus.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        user.deleteUser();
    }
}
