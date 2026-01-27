package com.peakle.shuttle.auth.key;
// kid -> Key ID - 어떤 키로 서명했는지 식별
// kty -> Key Type - "RSA"
// alg -> Algorithm - "RS256"
// use -> Usage - "sig" (서명용)
// n   -> Modulus - RSA 공개키의 n 값 (Base64URL)
// e   -> Exponent - RSA 공개키의 e 값 (Base64URL)
public record OidcPublicKey(
    String kid,
    String kty,
    String alg,
    String use,
    String n,
    String e
) {}
