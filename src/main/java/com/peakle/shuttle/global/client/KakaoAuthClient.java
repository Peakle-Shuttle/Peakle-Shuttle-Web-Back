package com.peakle.shuttle.global.client;

import com.peakle.shuttle.auth.key.OidcPublicKeyList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/** 카카오 OIDC 공개키 조회 Feign Client */
@FeignClient(name = "${client.kakao.name}", url = "${client.kakao.public-key-url}")
public interface KakaoAuthClient {
    @GetMapping
    OidcPublicKeyList getPublicKeys();
}
