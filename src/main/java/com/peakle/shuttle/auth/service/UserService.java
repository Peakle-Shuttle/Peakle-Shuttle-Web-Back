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
import com.peakle.shuttle.global.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserClientResponse getInfo(@NotNull Long code) {
        final User user = userRepository.findByUserCodeAndStatus(code, Status.ACTIVE)
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
                .createAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public boolean existsByUserId(@NotNull String userId) {
        return userRepository.existsByUserIdAndStatus(userId, Status.ACTIVE);
    }

    @Transactional(readOnly = true)
    public String findUserIdByEmail(@NotNull String userEmail) {
        final User user = userRepository.findByUserEmailAndStatus(userEmail, Status.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));
        return user.getUserId();
    }

    @Transactional
    public void changeId(@NotNull Long code, @NotNull UserIdRequest request) {
        final User user = userRepository.findByUserCodeAndStatus(code, Status.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        if (user.getUserId().equals(request.userId()) ||
            userRepository.existsByUserIdAndStatus(request.userId(), Status.ACTIVE)) {
            throw new AuthException(ExceptionCode.DUPLICATE_ID);
        }

        user.updateUserName(request.userId());
    }

    @Transactional
    public void changeEmail(@NotNull Long code, @NotNull UserEmailRequest request) {
        final User user = userRepository.findByUserCodeAndStatus(code, Status.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        if (user.getUserEmail().equals(request.userEmail()) ||
            userRepository.existsByUserEmailAndStatus(request.userEmail(), Status.ACTIVE)) {
            throw new AuthException(ExceptionCode.DUPLICATE_EMAIL);
        }

        user.updateUserEmail(request.userEmail());
    }

    @Transactional
    public void changePassword(@NotNull Long code, @NotNull UserPwRequest request) {
        final User user = userRepository.findByUserCodeAndStatus(code, Status.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.oldPassword(), user.getUserPassword())) {
            throw new AuthException(ExceptionCode.INVALID_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    public void updateInfo(@NotNull Long code, UserInfoRequest userInfoRequest) {

    }

    @Transactional
    public void removeUser(@NotNull Long code) {
        final User user = userRepository.findByUserCodeAndStatus(code, Status.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        user.deleteUser();
    }
}
