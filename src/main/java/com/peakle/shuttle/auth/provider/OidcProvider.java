package com.peakle.shuttle.auth.provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.peakle.shuttle.core.exception.extend.JwtException;
import com.peakle.shuttle.global.enums.ExceptionCode;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;



public interface OidcProvider {
    String getProviderId(String idToken);

    // 컴파일러가 readValue()로 생성되는 리턴타입 Map 몰라서 생기는 Warning 제거
    @SuppressWarnings("unchecked")
    default Map<String, String> parseHeaders(String token, ObjectMapper objectMapper) {
        String header = token.split("\\.")[0];

        try {
            return objectMapper.readValue(Base64.getUrlDecoder().decode(header), Map.class);
        } catch (IOException e) {
            throw new JwtException(ExceptionCode.EXTERNAL_SERVER_ERROR);
        }
        // 결과: { "kid": "abc123", "alg": "RS256", "typ": "JWT" }
    }
}