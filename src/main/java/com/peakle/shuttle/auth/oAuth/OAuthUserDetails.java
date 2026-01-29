package com.peakle.shuttle.auth.oAuth;

import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/** Spring Security 인증 주체 (UserDetails, OidcUser, OAuth2User 통합 구현) */
@Getter
public class OAuthUserDetails implements UserDetails, OidcUser, OAuth2User {
    private final AuthUserRequest user;
    private final Map<String, Object> attributes;


    /**
     * AuthUserRequest와 사용자 속성 맵을 사용해 OAuthUserDetails 인스턴스를 초기화한다.
     *
     * @param user       인증된 사용자 정보를 담은 DTO
     * @param attributes 사용자 속성(클레임)을 포함하는 키-값 맵
     */
    public OAuthUserDetails(AuthUserRequest user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    /**
     * 주어진 AuthUserRequest로 OAuthUserDetails 인스턴스를 생성한다.
     *
     * 내부 속성 맵(attributes)은 키 "code"에 user.code() 값을 넣어 초기화된다.
     *
     * @param user 인증된 사용자 정보를 담은 객체 — attributes의 "code"에 해당 사용자의 코드가 저장된다.
     */
    public OAuthUserDetails(AuthUserRequest user) {
        this.user = user;
        this.attributes = Map.of("code", user.code());
    }

    /**
     * 사용자 식별자 이름을 문자열 형태로 제공한다.
     *
     * @return 사용자 코드의 문자열 표현
     */
    @Override
    public String getName() {
        return user.code().toString();
    }

    /**
     * 사용자 속성(클레임)을 제공한다.
     *
     * OAuth2/OIDC 인증에서 제공된 원시 속성들을 키-값 맵으로 반환한다.
     *
     * @return 사용자 속성을 키(String)-값(Object) 쌍으로 보관한 맵
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * 사용자의 보안 역할을 기반으로 권한 컬렉션을 제공한다.
     *
     * @return `user.securityRole()`에서 생성된 단일 SimpleGrantedAuthority를 포함하는 권한 컬렉션
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.securityRole()));
    }

    /**
     * 사용자의 암호를 반환한다.
     *
     * @return 사용자 암호 문자열, 존재하지 않으면 {@code null}
     */
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * 사용자 식별에 사용되는 사용자 이름 문자열을 반환한다.
     *
     * @return {@code user.code()}의 문자열 표현
     */
    @Override
    public String getUsername() {
        return user.code().toString();
    }

    /**
     * 사용자 계정이 만료되지 않았는지 여부를 나타낸다.
     *
     * @return `true`이면 계정이 만료되지 않음, `false`이면 만료됨.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정이 잠겨 있지 않은지 확인합니다.
     *
     * @return `true`인 경우 계정이 잠겨 있지 않음, `false`인 경우 잠겨 있음.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 사용자 자격 증명의 만료 상태를 제공한다.
     *
     * @return `true`이면 사용자의 자격 증명이 만료되지 않았음을 나타내며, `false`이면 만료되었음을 나타낸다.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정의 활성화 상태를 나타낸다.
     *
     * @return `true`이면 계정이 활성화된 상태, `false`이면 비활성화된 상태.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * OIDC ID 토큰의 클레임을 제공한다.
     *
     * @return OIDC ID 토큰의 클레임을 담은 `Map<String, Object>` 또는 사용 불가한 경우 `null`
     */
    @Override
    public Map<String, Object> getClaims() {
        return null;
    }

    /**
     * OIDC 프로바이더가 제공하는 사용자 정보(UserInfo)를 반환한다.
     *
     * @return `OidcUserInfo` 객체(사용자 정보). 사용 가능한 정보가 없으면 `null`.
     */
    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    /**
     * 사용자의 OpenID Connect ID 토큰을 제공한다.
     *
     * @return `OidcIdToken` 사용자 ID 토큰 객체, ID 토큰이 설정되지 않았으면 `null`
     */
    @Override
    public OidcIdToken getIdToken() {
        return null;
    }
}