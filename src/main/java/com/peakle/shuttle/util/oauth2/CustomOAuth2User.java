package com.peakle.shuttle.util.oauth2;

import com.peakle.shuttle.entity.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final Long userCode;
    private final String userId;
    private final Role userRole;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(Long userCode, String userId, Role userRole, Map<String, Object> attributes) {
        this.userCode = userCode;
        this.userId = userId;
        this.userRole = userRole;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(userRole.getKey()));
    }

    @Override
    public String getName() {
        return userId;
    }
}
