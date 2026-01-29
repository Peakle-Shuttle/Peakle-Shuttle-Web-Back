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
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 주어진 사용자 ID로 활성 사용자의 존재 여부를 확인합니다.
     *
     * @param userId 확인할 사용자 ID
     * @return 활성 상태인 사용자가 존재하면 `true`, 그렇지 않으면 `false`
     */
    @Transactional(readOnly = true)
    public boolean existsByUserId(@NotNull String userId) {
        return userRepository.existsByUserIdAndStatus(userId, Status.ACTIVE);
    }

    /**
     * 이메일로 사용자 ID를 조회합니다.
     *
     * @param userEmail 조회할 이메일
     * @return 해당 이메일로 등록된 사용자 ID
     * @throws AuthException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public String findUserIdByEmail(@NotNull String userEmail) {
        final User user = userRepository.findByUserEmailAndStatus(userEmail, Status.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));
        return user.getUserId();
    }

    /**
     * 사용자의 로그인 ID를 새 값으로 변경합니다.
     *
     * @param code 사용자 고유 코드(활성 상태인 사용자 조회에 사용)
     * @param request 변경할 새 사용자 ID를 포함한 요청 객체
     * @throws AuthException 사용자가 존재하지 않을 때(NOT_FOUND_USER) 또는 요청한 ID가 현재와 같거나 다른 활성 사용자에 의해 이미 사용 중일 때(DUPLICATE_ID)
     */
    @Transactional
    public void changeId(@NotNull Long code, @NotNull UserIdRequest request) {
        final User user = userRepository.findByUserCodeAndStatus(code, Status.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        if (user.getUserId().equals(request.userId()) ||
            userRepository.existsByUserIdAndStatus(request.userId(), Status.ACTIVE)) {
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
        final User user = userRepository.findByUserCodeAndStatus(code, Status.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        if (Objects.equals(user.getUserEmail(), request.userEmail()) ||
            userRepository.existsByUserEmailAndStatus(request.userEmail(), Status.ACTIVE)) {
            throw new AuthException(ExceptionCode.DUPLICATE_EMAIL);
        }

        user.updateUserEmail(request.userEmail());
    }

    /**
     * 사용자의 기존 비밀번호를 검증한 후 새 비밀번호로 변경한다.
     *
     * @param code    사용자 고유 코드
     * @param request 변경에 필요한 기존 비밀번호와 새 비밀번호를 포함한 요청 객체
     * @throws AuthException 사용자가 존재하지 않거나 기존 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    public void changePassword(@NotNull Long code, @NotNull UserPwRequest request) {
        final User user = userRepository.findByUserCodeAndStatus(code, Status.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        if (!passwordEncoder.matches(request.oldPassword(), user.getUserPassword())) {
            throw new AuthException(ExceptionCode.INVALID_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));
    }

    /**
     * 사용자 정보를 갱신합니다.
     *
     * <p>현재 구현되어 있지 않으며 호출 시 {@link UnsupportedOperationException}을 던집니다.</p>
     *
     * @param code 업데이트 대상 사용자의 고유 코드
     * @param userInfoRequest 업데이트할 사용자 정보가 담긴 요청 DTO
     * @throws UnsupportedOperationException 이 메서드는 미구현이므로 호출 시 항상 발생합니다
     */
    @Transactional
    public void updateInfo(@NotNull Long code, UserInfoRequest userInfoRequest) {
        throw new UnsupportedOperationException("updateInfo not implemented");
    }

    /**
     * 회원 탈퇴를 처리합니다 (소프트 삭제).
     *
     * @param code 사용자 고유 코드
     * @throws AuthException 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public void removeUser(@NotNull Long code) {
        final User user = userRepository.findByUserCodeAndStatus(code, Status.ACTIVE)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        user.deleteUser();
    }
}