package com.peakle.shuttle.auth.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peakle.shuttle.auth.dto.JwtProperties;
import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.auth.dto.response.TokenResponse;
import com.peakle.shuttle.core.exception.JwtException;
import com.peakle.shuttle.global.enums.ExceptionCode;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
//    private final ObjectMapper objectMapper;
    private SecretKey key;
    private static final String AUTHORITIES_KEY = "role";
    private static final String USER_ID_KEY = "userCode";

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder()
                .encodeToString(jwtProperties.getSecret().getBytes()));
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenResponse createTokenResponse(AuthUserRequest user) {
        Date now = new Date();

        if (isNull(user)) {
            throw new JwtException(ExceptionCode.EMPTY_USER);
        }

        try {
            final String accessToken =
                    Jwts.builder()
                            .claim(USER_ID_KEY, user.id())
                            .claim(AUTHORITIES_KEY, user.securityRole())
                            .issuedAt(now)
                            .expiration(new Date(now.getTime() + jwtProperties.getAccessTokenValidity()))
                            .signWith(key)
                            .compact();

            final String refreshToken =
                    Jwts.builder()
                            .claim(USER_ID_KEY, user.id())
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
                            .claim(USER_ID_KEY, user.id())
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
    private boolean isSameDate(String token) {
        return LocalDate.ofInstant(
                        parseClaims(token).getExpiration().toInstant(),
                        ZoneId.systemDefault()
                )
                .equals(LocalDate.now());
    }

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
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

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
//
//
//    public Long getUserIdFromToken(String token) {
//        Claims claims = parseClaims(token);
//        return claims.get(USER_ID_KEY, Long.class);
//    }
//
//    public long getRefreshTokenValidity() {
//        return jwtProperties.getRefreshTokenValidity();
//    }
}
