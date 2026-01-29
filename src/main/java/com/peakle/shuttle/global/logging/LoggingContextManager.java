package com.peakle.shuttle.global.logging;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** MDC 기반 로깅 컨텍스트 관리자 (요청 ID, 사용자 정보, 기능명) */
@Component
public class LoggingContextManager {

    private static final String MDC_REQUEST_ID = "requestId";
    private static final String MDC_USER_INFO = "userInfo";
    private static final String MDC_FEATURE = "feature";

    /**
     * MDC에 요청 식별자(request ID)를 설정한다.
     *
     * 생성된 8자 길이의 요청 식별자를 MDC의 `MDC_REQUEST_ID` ("requestId") 키에 저장한다.
     */
    public void setRequestId() {
        MDC.put(MDC_REQUEST_ID, UUID.randomUUID().toString().substring(0, 8));
    }

    /**
     * MDC에 사용자 식별 정보를 저장한다.
     *
     * @param userId 저장할 사용자 ID. null인 경우 문자열 "null"이 MDC에 저장된다.
     */
    public void setUserInfo(Long userId) {
        MDC.put(MDC_USER_INFO, String.valueOf(userId));
    }

    /**
     * 로깅 MDC에 기능 이름(feature)을 설정합니다.
     *
     * @param feature 로그에 포함할 기능 이름
     */
    public void setFeature(String feature) {
        MDC.put(MDC_FEATURE, feature);
    }

    /**
     * 현재 MDC에서 요청 식별자(requestId)를 조회한다.
     *
     * @return MDC에 저장된 요청 식별자 문자열, 없으면 {@code null}
     */
    public String getRequestId() {
        return MDC.get(MDC_REQUEST_ID);
    }

    /**
     * MDC에 저장된 사용자 식별 정보를 가져옵니다.
     *
     * @return MDC 키 "userInfo"에 설정된 사용자 식별 문자열, 설정되어 있지 않으면 `null`
     */
    public String getUserInfo() {
        return MDC.get(MDC_USER_INFO);
    }

    /**
     * MDC에 설정된 모든 컨텍스트 항목을 제거한다.
     *
     * <p>현재 스레드의 SLF4J MDC(mapped diagnostic context)에 저장된 모든 키-값 쌍을 비운다.</p>
     */
    public void clear() {
        MDC.clear();
    }
}