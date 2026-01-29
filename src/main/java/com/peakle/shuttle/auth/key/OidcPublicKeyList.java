package com.peakle.shuttle.auth.key;

import com.peakle.shuttle.core.exception.extend.JwtException;
import com.peakle.shuttle.global.enums.ExceptionCode;

import java.util.List;
import java.util.Objects;

/** OIDC 공개키 목록 (kid/alg 매칭으로 공개키 조회) */
public record OidcPublicKeyList(
        List<OidcPublicKey> keys
) {
    /**
     * 주어진 `kid`와 `alg`에 일치하는 OIDC 공개 키를 조회한다.
     *
     * @param kid 검색할 공개 키의 키 식별자(`kid`)
     * @param alg 검색할 공개 키의 알고리즘(`alg`)
     * @return 일치하는 `OidcPublicKey` 객체
     * @throws JwtException 키 목록이 없거나 비어있거나, 해당 `kid`와 `alg`에 일치하는 키를 찾지 못한 경우
     */
    public OidcPublicKey getMatchedKey(String kid, String alg) {
        if (keys == null || keys.isEmpty()) {
            throw new JwtException(ExceptionCode.EXTERNAL_SERVER_ERROR);
        }
        return keys.stream()
                .filter(key -> Objects.equals(key.kid(), kid) && Objects.equals(key.alg(), alg))
                .findAny()
                .orElseThrow(() -> new JwtException(ExceptionCode.EXTERNAL_SERVER_ERROR));
    }
}