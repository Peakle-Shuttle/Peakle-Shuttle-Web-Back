package com.peakle.shuttle.auth.key;

import com.peakle.shuttle.core.exception.extend.JwtException;
import com.peakle.shuttle.global.enums.ExceptionCode;

import java.util.List;
import java.util.Objects;

/** OIDC 공개키 목록 (kid/alg 매칭으로 공개키 조회) */
public record OidcPublicKeyList(
        List<OidcPublicKey> keys
) {
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
