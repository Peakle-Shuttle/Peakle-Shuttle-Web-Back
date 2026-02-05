package com.peakle.shuttle.admin.user;

import com.peakle.shuttle.admin.user.dto.AdminUserListResponse;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.global.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;

    /**
     * 활성 사용자 목록을 조회합니다.
     *
     * @return 사용자 목록
     */
    public List<AdminUserListResponse> getUsers() {
        return userRepository.findAllByStatus(UserStatus.ACTIVE).stream()
                .map(AdminUserListResponse::from)
                .toList();
    }
}
