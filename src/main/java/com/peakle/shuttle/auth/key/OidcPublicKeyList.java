package com.peakle.shuttle.auth.key;

import com.peakle.shuttle.core.exception.extend.JwtException;
import com.peakle.shuttle.global.enums.ExceptionCode;

import java.util.List;

public record OidcPublicKeyList(
        List<OidcPublicKey> keys
) {
    public OidcPublicKey getMatchedKey(String kid, String alg) {
        return keys.stream()
                .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                .findAny()
                .orElseThrow(() -> new JwtException(ExceptionCode.EXTERNAL_SERVER_ERROR));
    }
}
