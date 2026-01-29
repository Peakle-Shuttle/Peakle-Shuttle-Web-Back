package com.peakle.shuttle.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/** 민감 정보를 마스킹하여 로깅하는 유틸리티 */
@Component
public class SensitiveLogger {
    private static final Set<String> SENSITIVE_FIELDS =
            Set.of(
                    "password", "pw", "token", "secret", "key", "credential",
                    "providerToken", "deviceToken", "refreshToken", "accessToken"
            );

    private static final Set<String> SENSITIVE_CLASS_NAMES =
            Set.of(
                    "AdminLoginRequest", "UserSignInRequest", "UserSignUpRequest",
                    "TokenCreateRequest", "UserDeviceRequest", "AuthUserRequest"
            );

    /**
     * 로그용 인수 배열을 마스킹하여 문자열로 포맷한다.
     *
     * @param args 로그에 기록할 인수 배열 — 각 요소는 민감 정보가 마스킹되거나 축약된 형태로 변환되어 사용된다.
     * @return 대괄호로 감싼 인수 목록 문자열. 민감한 클래스는 클래스명 + "[MASKED]"로 표기되고,
     *         객체의 문자열 표현에 민감 필드가 포함되면 클래스명 + "[CONTAINS_SENSITIVE_DATA]"로 표시되며,
     *         HttpServletRequest와 MultipartFile은 각각 고정된 표기로 대체되고 긴 문자열은 잘려서 반환된다.
     */
    public String sensitiveArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return "[" + Arrays.stream(args)
                .map(this::sensitiveObject)
                .collect(Collectors.joining(", ")) + "]";
    }

    /**
     * 로그 출력용으로 객체를 마스킹하거나 축약된 문자열 표현으로 변환한다.
     *
     * 변환 규칙:
     * - null이면 "null"을 반환한다.
     * - HttpServletRequest이면 "HttpServletRequest"를 반환한다.
     * - 클래스 이름에 "MultipartFile"이 포함되면 "MultipartFile"을 반환한다.
     * - 민감한 클래스면 `ClassName[MASKED]` 형태를 반환한다.
     * - 객체의 문자열 표현에 민감 필드 토큰이 포함되면 `ClassName[CONTAINS_SENSITIVE_DATA]`를 반환한다.
     * - 문자열 길이가 200자를 초과하면 처음 200자 뒤에 "..."을 붙여 반환한다.
     * - 그 외에는 객체의 toString() 결과를 반환한다.
     *
     * @param obj 변환할 객체 (null 허용)
     * @return 위 규칙에 따라 마스킹되거나 축약된 문자열 표현
     */
    private String sensitiveObject(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof HttpServletRequest) {
            return "HttpServletRequest";
        }

        if (obj.getClass().getName().contains("MultipartFile")) {
            return "MultipartFile";
        }

        String className = obj.getClass().getSimpleName();

        if (isSensitiveClass(className)) {
            return className + "[MASKED]";
        }

        String objString = obj.toString();
        if (containsSensitiveData(objString)) {
            return className + "[CONTAINS_SENSITIVE_DATA]";
        }

        if (objString.length() > 200) {
            return objString.substring(0, 200) + "...";
        }

        return objString;
    }

    /**
     * 주어진 클래스 이름이 민감한 정보가 포함되거나 민감한 클래스 목록에 해당하는지 판단한다.
     *
     * @param className 검사할 클래스의 단순 이름
     * @return `true`이면 클래스 이름이 민감한 클래스 집합에 포함되거나 소문자로 변환했을 때 "auth", "token", "login", "password" 중 하나를 포함하는 경우, `false` 그렇지 않은 경우
     */
    private boolean isSensitiveClass(String className) {
        return SENSITIVE_CLASS_NAMES.contains(className) ||
                className.toLowerCase().contains("auth") ||
                className.toLowerCase().contains("token") ||
                className.toLowerCase().contains("login") ||
                className.toLowerCase().contains("password");
    }

    /**
     * 문자열에 민감한 필드 패턴(예: `password=` 또는 `token:`)이 포함되어 있는지 확인합니다.
     *
     * @param str 검사할 문자열
     * @return `true`이면 문자열에 설정된 민감 필드 토큰 뒤에 `=` 또는 `:` 패턴이 포함되어 있는 경우, `false`는 그 외의 경우
     */
    private boolean containsSensitiveData(String str) {
        String lowerStr = str.toLowerCase();
        return SENSITIVE_FIELDS.stream()
                .anyMatch(field -> lowerStr.contains(field + "=") || lowerStr.contains(field + ":"));
    }
}