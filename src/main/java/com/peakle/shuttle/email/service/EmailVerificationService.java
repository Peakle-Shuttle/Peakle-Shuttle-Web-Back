package com.peakle.shuttle.email.service;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.core.exception.extend.InvalidArgumentException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.global.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final StringRedisTemplate stringRedisTemplate;
    private final EmailSendService emailSendService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String CODE_PREFIX = "email:code:";
    private static final String VERIFIED_PREFIX = "email:verified:";
    private static final String RATE_LIMIT_PREFIX = "email:rate:";
    private static final String FIND_ID_CODE_PREFIX = "email:code:find-id:";
    private static final String RESET_PW_CODE_PREFIX = "email:code:reset-pw:";
    private static final String RESET_PW_VERIFIED_PREFIX = "email:verified:reset-pw:";
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration VERIFIED_TTL = Duration.ofMinutes(10);
    private static final Duration RATE_LIMIT_TTL = Duration.ofSeconds(60);

    public void sendVerificationCode(String email) {
        if (userRepository.existsByUserEmailAndUserStatus(email, UserStatus.ACTIVE)) {
            throw new InvalidArgumentException(ExceptionCode.DUPLICATE_EMAIL);
        }

        String rateLimitKey = RATE_LIMIT_PREFIX + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(rateLimitKey))) {
            throw new InvalidArgumentException(ExceptionCode.EMAIL_RATE_LIMIT);
        }

        String code = generateCode();
        stringRedisTemplate.opsForValue().set(CODE_PREFIX + email, code, CODE_TTL);
        stringRedisTemplate.opsForValue().set(rateLimitKey, "1", RATE_LIMIT_TTL);

        emailSendService.sendVerificationEmail(email, code);
    }

    public void verifyCode(String email, String code) {
        String storedCode = stringRedisTemplate.opsForValue().get(CODE_PREFIX + email);

        if (storedCode == null) {
            throw new InvalidArgumentException(ExceptionCode.EMAIL_CODE_EXPIRED);
        }

        if (!storedCode.equals(code)) {
            throw new InvalidArgumentException(ExceptionCode.EMAIL_CODE_INVALID);
        }

        stringRedisTemplate.delete(CODE_PREFIX + email);
        stringRedisTemplate.opsForValue().set(VERIFIED_PREFIX + email, "true", VERIFIED_TTL);
    }

    public boolean isEmailVerified(String email) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(VERIFIED_PREFIX + email));
    }

    public void consumeVerification(String email) {
        stringRedisTemplate.delete(VERIFIED_PREFIX + email);
    }

    // === 아이디 찾기 ===

    public void sendFindIdCode(String userName, String userNumber, String email) {
        User user = userRepository.findByUserNameAndUserNumberAndUserEmailAndUserStatus(
                userName, userNumber, email, UserStatus.ACTIVE
        ).orElseThrow(() -> new InvalidArgumentException(ExceptionCode.USER_INFO_NOT_MATCH));

        if (user.getUserId().startsWith("kakao_")) {
            throw new AuthException(ExceptionCode.ANOTHER_PROVIDER);
        }

        checkRateLimit(email);

        String code = generateCode();
        stringRedisTemplate.opsForValue().set(FIND_ID_CODE_PREFIX + email, code, CODE_TTL);
        stringRedisTemplate.opsForValue().set(RATE_LIMIT_PREFIX + email, "1", RATE_LIMIT_TTL);

        emailSendService.sendVerificationEmail(email, code);
    }

    public String verifyFindIdCode(String email, String code) {
        String storedCode = stringRedisTemplate.opsForValue().get(FIND_ID_CODE_PREFIX + email);

        if (storedCode == null) {
            throw new InvalidArgumentException(ExceptionCode.EMAIL_CODE_EXPIRED);
        }

        if (!storedCode.equals(code)) {
            throw new InvalidArgumentException(ExceptionCode.EMAIL_CODE_INVALID);
        }

        stringRedisTemplate.delete(FIND_ID_CODE_PREFIX + email);

        User user = userRepository.findByUserEmailAndUserStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new InvalidArgumentException(ExceptionCode.NOT_FOUND_USER));

        return user.getUserId();
    }

    // === 비밀번호 재설정 ===

    public void sendResetPwCode(String userName, String userNumber, String userId, String email) {
        userRepository.findByUserNameAndUserNumberAndUserIdAndUserEmailAndUserStatus(
                userName, userNumber, userId, email, UserStatus.ACTIVE
        ).orElseThrow(() -> new InvalidArgumentException(ExceptionCode.USER_INFO_NOT_MATCH));

        checkRateLimit(email);

        String code = generateCode();
        stringRedisTemplate.opsForValue().set(RESET_PW_CODE_PREFIX + email, code, CODE_TTL);
        stringRedisTemplate.opsForValue().set(RATE_LIMIT_PREFIX + email, "1", RATE_LIMIT_TTL);

        emailSendService.sendVerificationEmail(email, code);
    }

    public void verifyResetPwCode(String email, String code) {
        String storedCode = stringRedisTemplate.opsForValue().get(RESET_PW_CODE_PREFIX + email);

        if (storedCode == null) {
            throw new InvalidArgumentException(ExceptionCode.EMAIL_CODE_EXPIRED);
        }

        if (!storedCode.equals(code)) {
            throw new InvalidArgumentException(ExceptionCode.EMAIL_CODE_INVALID);
        }

        stringRedisTemplate.delete(RESET_PW_CODE_PREFIX + email);
        stringRedisTemplate.opsForValue().set(RESET_PW_VERIFIED_PREFIX + email, "true", VERIFIED_TTL);
    }

    public void resetPassword(String email, String newPassword) {
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(RESET_PW_VERIFIED_PREFIX + email))) {
            throw new InvalidArgumentException(ExceptionCode.EMAIL_NOT_VERIFIED);
        }

        User user = userRepository.findByUserEmailAndUserStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new InvalidArgumentException(ExceptionCode.NOT_FOUND_USER));

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        stringRedisTemplate.delete(RESET_PW_VERIFIED_PREFIX + email);
    }

    // === 공통 ===

    private void checkRateLimit(String email) {
        String rateLimitKey = RATE_LIMIT_PREFIX + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(rateLimitKey))) {
            throw new InvalidArgumentException(ExceptionCode.EMAIL_RATE_LIMIT);
        }
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
