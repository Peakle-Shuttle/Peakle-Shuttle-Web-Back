package com.peakle.shuttle.admin.user;

import com.peakle.shuttle.admin.user.dto.AdminUserListResponse;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.global.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;

    public List<AdminUserListResponse> getUsers() {
        return userRepository.findAllByStatus(Status.ACTIVE).stream()
                .map(AdminUserListResponse::from)
                .toList();
    }
}
