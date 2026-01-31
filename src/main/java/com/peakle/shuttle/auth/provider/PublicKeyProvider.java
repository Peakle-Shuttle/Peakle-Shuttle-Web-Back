package com.peakle.shuttle.auth.provider;

import com.peakle.shuttle.auth.key.OidcPublicKey;
import com.peakle.shuttle.auth.key.OidcPublicKeyList;
import com.peakle.shuttle.core.exception.extend.JwtException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

/** OIDC 공개키를 기반으로 RSA PublicKey를 생성하는 Provider */
@Component
public class PublicKeyProvider {
    /**
     * 토큰 헤더의 kid/alg에 매칭되는 공개키를 생성합니다.
     *
     * @param tokenHeaders JWT 토큰 헤더 (kid, alg 포함)
     * @param publicKeyList OIDC 공개키 목록
     * @return 생성된 RSA PublicKey
     */
    public PublicKey generatePublicKey(final Map<String, String> tokenHeaders, final OidcPublicKeyList publicKeyList) {
        final OidcPublicKey publicKey = publicKeyList.getMatchedKey(tokenHeaders.get("kid"), tokenHeaders.get("alg"));

        if (publicKey == null) {
            throw new JwtException(ExceptionCode.EXTERNAL_SERVER_ERROR);
        }

        return getPublicKey(publicKey);
    }

    /**
     * OIDC 공개키 정보로부터 RSA PublicKey를 생성합니다.
     *
     * @param publicKey OIDC 공개키 (n, e, kty 포함)
     * @return 생성된 RSA PublicKey
     * @throws JwtException 알고리즘이나 키 스펙이 유효하지 않은 경우
     */
    public PublicKey getPublicKey(final OidcPublicKey publicKey) {
        final byte[] nBytes = Base64.getUrlDecoder().decode(publicKey.n());
        final byte[] eBytes = Base64.getUrlDecoder().decode(publicKey.e());

        final RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(1, nBytes), new BigInteger(1, eBytes));

        try {
            return KeyFactory.getInstance(publicKey.kty()).generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JwtException(ExceptionCode.EXTERNAL_SERVER_ERROR);
        }
    }
}
