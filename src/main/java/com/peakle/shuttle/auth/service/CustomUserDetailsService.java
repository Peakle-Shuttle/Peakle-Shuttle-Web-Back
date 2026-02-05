package com.peakle.shuttle.auth.service;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.global.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자 ID로 UserDetails를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 조회된 사용자의 UserDetails
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserIdAndUserStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getUserPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole().name()))
        );
    }
}
