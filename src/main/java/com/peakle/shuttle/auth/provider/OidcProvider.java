package com.peakle.shuttle.auth.provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.peakle.shuttle.core.exception.extend.JwtException;
import com.peakle.shuttle.global.enums.ExceptionCode;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;



/** OIDC 인증 Provider 인터페이스 (ID Token에서 사용자 고유 ID 추출) */
public interface OidcProvider {
    /**
 * 주어진 ID 토큰에서 공급자 고유의 사용자 식별자를 추출한다.
 *
 * @param idToken 공급자가 발행한 ID 토큰(JWT)
 * @return 해당 공급자에서 식별하는 고유한 사용자 ID 문자열
 */
String getProviderId(String idToken);

    /**
     * JWT 토큰의 헤더(첫 번째 세그먼트)를 파싱하여 헤더 클레임을 문자열 맵으로 반환한다.
     *
     * @param token JWT 형식의 토큰(헤더.페이로드.서명). 유효하지 않은 형식이면 예외가 발생한다.
     * @param objectMapper JSON 바디를 Map으로 변환하는 데 사용되는 ObjectMapper
     * @return JWT 헤더 클레임을 담은 Map<String, String> (예: { "kid": "abc123", "alg": "RS256", "typ": "JWT" })
     * @throws JwtException 토큰이 null이거나 형식이 올바르지 않거나 헤더 디코딩/파싱에 실패한 경우
     */
    @SuppressWarnings("unchecked")
    default Map<String, String> parseHeaders(String token, ObjectMapper objectMapper) {
        if (token == null || !token.contains(".")) {
            throw new JwtException(ExceptionCode.TOKEN_NOT_VALID);
        }
        String header = token.split("\\.")[0];

        try {
            return objectMapper.readValue(Base64.getUrlDecoder().decode(header), Map.class);
        } catch (IOException e) {
            throw new JwtException(ExceptionCode.TOKEN_NOT_VALID);
        }
        // 결과: { "kid": "abc123", "alg": "RS256", "typ": "JWT" }
    }
}