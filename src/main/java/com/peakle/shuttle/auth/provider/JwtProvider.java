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
import java.util.Objects;
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
     * Access Token을 재생성합니다. Refresh Token 만료일이 오늘이면 Refresh Token도 함께 갱신합니다.
     *
     * @param user 인증된 사용자 정보
     * @param refreshToken 기존 Refresh Token
     * @return 재생성된 토큰 응답
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
     * Refresh Token의 만료일이 오늘인지 확인합니다.
     *
     * @param token JWT 토큰
     * @return 만료일이 오늘이면 true
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
     * 외부 PublicKey로 JWT 토큰을 파싱하여 Claims를 반환합니다 (OAuth/OIDC용).
     *
     * @param token JWT 토큰 문자열
     * @param publicKey 검증에 사용할 공개키
     * @return 파싱된 Claims
     * @throws JwtException 토큰이 만료되었거나 유효하지 않은 경우
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
     * 토큰이 만료되었는지 확인합니다.
     *
     * @param token JWT 토큰 문자열
     * @return 만료되었으면 true
     * @throws JwtException 토큰 형식이 잘못된 경우
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
     * JWT 토큰에서 인증 정보를 추출하여 Authentication 객체를 생성합니다.
     *
     * @param token JWT 토큰 문자열
     * @return Spring Security Authentication 객체
     * @throws JwtException 권한 정보가 없는 경우
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

//        Long userCode = Long.parseLong(claims.get(USER_ID_KEY).toString());
        Object userCodeClaim = claims.get(USER_ID_KEY);
        if (userCodeClaim == null) {
            throw new JwtException(ExceptionCode.EMPTY_USER);
        }

        Long userCode;
        try {
            userCode = Long.parseLong(userCodeClaim.toString());
        }  catch (NumberFormatException e) {
            throw new JwtException(ExceptionCode.WRONG_JWT_TOKEN);
        }

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
