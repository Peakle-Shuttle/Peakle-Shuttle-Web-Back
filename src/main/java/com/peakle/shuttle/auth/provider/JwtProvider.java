package com.peakle.shuttle.auth.provider;

import com.peakle.shuttle.auth.dto.JwtProperties;
import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.auth.dto.response.TokenResponse;
import com.peakle.shuttle.core.exception.extend.JwtException;
import com.peakle.shuttle.auth.oAuth.OAuthUserDetails;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.global.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/** JWT 토큰 생성, 파싱, 검증을 담당하는 Provider */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
//    private final ObjectMapper objectMapper;
    private SecretKey key;
    private static final String AUTHORITIES_KEY = "role";
    private static final String USER_ID_KEY = "userCode";

    /** JWT 서명용 SecretKey를 초기화합니다. */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder()
                .encodeToString(jwtProperties.getSecret().getBytes()));
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token과 Refresh Token을 새로 생성합니다.
     *
     * @param user 인증된 사용자 정보 (code, role)
     * @return 생성된 토큰 응답
     * @throws JwtException 사용자가 null이거나 키가 유효하지 않은 경우
     */
    public TokenResponse createTokenResponse(AuthUserRequest user) {
        Date now = new Date();

        if (isNull(user)) {
            throw new JwtException(ExceptionCode.EMPTY_USER);
        }

        try {
            final String accessToken =
                    Jwts.builder()
                            .claim(USER_ID_KEY, user.code())
                            .claim(AUTHORITIES_KEY, user.securityRole())
                            .issuedAt(now)
                            .expiration(new Date(now.getTime() + jwtProperties.getAccessTokenValidity()))
                            .signWith(key)
                            .compact();

            final String refreshToken =
                    Jwts.builder()
                            .claim(USER_ID_KEY, user.code())
                            .claim(AUTHORITIES_KEY, user.securityRole())
                            .issuedAt(now)
                            .expiration(new Date(now.getTime() + jwtProperties.getRefreshTokenValidity()))
                            .signWith(key)
                            .compact();

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (InvalidKeyException e) {
            throw new JwtException(ExceptionCode.INVALID_KEY);
        }
    }
    /**
         * 사용자 정보와 기존 리프레시 토큰을 바탕으로 새로운 액세스 토큰을 생성하며,
         * 리프레시 토큰의 만료일이 오늘이면 리프레시 토큰도 함께 재발급합니다.
         *
         * @param user 인증된 사용자 정보
         * @param refreshToken 기존 리프레시 토큰
         * @return accessToken과 refreshToken을 포함한 TokenResponse 객체
         */
    public TokenResponse recreateTokenResponse(AuthUserRequest user, String refreshToken) {
        Date now = new Date();
        if (isNull(user)) {
            throw new JwtException(ExceptionCode.EMPTY_USER);
        }

        try {

            if (isSameDate(refreshToken)) {
                return createTokenResponse(user);
            }

            final String accessToken =
                    Jwts.builder()
                            .claim(USER_ID_KEY, user.code())
                            .claim(AUTHORITIES_KEY, user.securityRole())
                            .issuedAt(now)
                            .expiration(new Date(now.getTime() + jwtProperties.getAccessTokenValidity()))
                            .signWith(key)
                            .compact();

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (InvalidKeyException e) {
            throw new JwtException(ExceptionCode.INVALID_KEY);
        }
    }
    /**
     * 토큰의 만료일이 현재 날짜(오늘)와 동일한지 검사합니다.
     *
     * @param token 만료일을 검사할 JWT 문자열
     * @return `true`이면 토큰의 만료일이 오늘과 같고, `false`이면 그렇지 않습니다.
     */
    private boolean isSameDate(String token) {
        return LocalDate.ofInstant(
                        parseClaims(token).getExpiration().toInstant(),
                        ZoneId.systemDefault()
                )
                .equals(LocalDate.now());
    }

    /**
     * 내부 SecretKey로 JWT 토큰을 파싱하여 Claims를 반환합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 파싱된 Claims
     * @throws JwtException 토큰이 만료되었거나 유효하지 않은 경우
     */
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new JwtException(ExceptionCode.EXPIRED_JWT_TOKEN);
        } catch (RuntimeException e) {
            throw new JwtException(ExceptionCode.WRONG_JWT_TOKEN);
        }
    }

    /**
         * 외부 PublicKey로 서명된 JWT의 클레임을 검증하고 파싱합니다 (OAuth/OIDC용).
         *
         * @param token     JWT 토큰 문자열
         * @param publicKey 검증에 사용할 공개키
         * @return 파싱된 Claims 객체
         * @throws JwtException 토큰이 만료된 경우(EXPIRED_JWT_TOKEN) 또는 토큰이 유효하지 않은 경우(WRONG_JWT_TOKEN)
         */
    public Claims parseClaims(String token, PublicKey publicKey) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new JwtException(ExceptionCode.EXPIRED_JWT_TOKEN);
        } catch (RuntimeException e) {
            throw new JwtException(ExceptionCode.WRONG_JWT_TOKEN);
        }
    }

    /**
         * 토큰이 만료되었는지 검사합니다.
         *
         * @param token 검사할 JWT 문자열
         * @return `true`이면 토큰이 만료된 상태, `false`이면 만료되지 않은 상태
         * @throws JwtException 형식이나 서명이 올바르지 않아 토큰을 파싱할 수 없을 때 발생합니다.
         */
    public boolean expired(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (io.jsonwebtoken.JwtException e) {
            throw new JwtException(ExceptionCode.WRONG_JWT_TOKEN);
        }
    }

    /**
         * JWT 토큰에서 사용자 식별자와 권한을 추출하여 Spring Security Authentication 객체를 생성합니다.
         *
         * @param token 서명된 JWT 토큰 문자열
         * @return 사용자 정보와 권한이 포함된 Authentication 객체
         * @throws JwtException 권한(claim "role") 정보가 존재하지 않을 경우
         */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new JwtException(ExceptionCode.NO_AUTHORITIES_KEY);
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        Long userCode = Long.parseLong(claims.get(USER_ID_KEY).toString());
        Role role = Role.fromKey(authorities.iterator().next().getAuthority());
        OAuthUserDetails principal = new OAuthUserDetails(new AuthUserRequest(userCode, role));

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}