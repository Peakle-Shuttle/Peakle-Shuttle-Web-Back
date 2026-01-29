package com.peakle.shuttle.global.enums;

import lombok.Getter;

/** 애플리케이션 전역 예외 코드 정의 (HTTP 상태 코드 + 메시지) */
@Getter
public enum ExceptionCode {

    // Auth
    INVALID_KEY("400", "잘못된 KEY 입니다"),
    INVALID_PROVIDER("400","지원하지 않는 PROVIDER 입니다."),
    EXPIRED_JWT_TOKEN("400", "만료된 JWT 입니다."),
    EXPIRED_REFRESH_TOKEN("401", "재 인증이 필요합니다."),
    NOT_EXIST_BEARER_SUFFIX("400", "Bearer 접두사가 포함되지 않았습니다."),
    WRONG_JWT_TOKEN("400", "잘못된 JWT 입니다."),
    EMPTY_AUTH_JWT("400", "인증 정보가 비어있는 JWT 입니다."),
    EMPTY_USER("400", "비어있는 유저 정보로 JWT를 생성할 수 없습니다."),
    EMPTY_ACCESS("400", "액세스 토큰이 존재하지 않습니다."),
    EMPTY_REFRESH("400", "리프레시 토큰이 존재하지 않습니다."),
    ANOTHER_PROVIDER("400", "로그인 제공자가 다릅니다."),
    TOKEN_NOT_VALID("400", "ID TOKEN 인증에 실패하였습니다."),
    DUPLICATE_ID("403", "이미 사용 중인 아이디입니다."),
    DUPLICATE_NICKNAME("403", "이미 사용 중인 닉네임입니다."),
    DUPLICATE_EMAIL("403", "이미 사용 중인 이메일입니다."),
    NOT_FOUND_USER("404", "사용자 정보를 찾을 수 없습니다."),
    NO_AUTHORITIES_KEY("403", "권한 정보가 없는 토큰입니다."),
    INVALID_PASSWORD("400", "비밀번호가 일치하지 않습니다."),

    // Policy

    // Post
    NOT_FOUND_POST("404", "게시글을 찾을 수 없습니다."),

    // Comment
    NOT_FOUND_COMMENT("404", "댓글을 찾을 수 없습니다."),
    NOT_PARENT_COMMENT("400", "부모 댓글이 아닙니다."),
    NOT_FOUND_COMMENT_STATS("404", "댓글 정보를 찾을 수 없습니다."),

    // Notice
    NOT_FOUND_NOTICE("404", "공지를 찾을 수 없습니다."),

    // Image
    NOT_IMAGE_REQUEST("400", "이미지 파일만 업로드 할 수 있습니다."),

    // Request
    WRONG_PARAMETER("400", "잘못된 파라미터 입니다."),
    METHOD_NOT_ALLOWED("405", "허용되지 않은 메소드 입니다."),
    REQUEST_CONFLICT("409", "새로고침 후 다시 시도해주세요."),

    // Server Error
    INTERNAL_SERVER_ERROR("500", "내부 서버 에러 입니다."),
    EXTERNAL_SERVER_ERROR("500", "외부 서버 에러 입니다."),
    ILLEGAL_HANDLER_TYPE("500", "핸들러를 처리할 수 없습니다."),
    ILLEGAL_EVENT("500", "이벤트를 처리할 수 없습니다.");

    private final String code;
    private final String message;

    /**
     * 코드와 메시지로 예외 코드 열거형 상수의 필드를 초기화한다.
     *
     * @param code    HTTP 유사 상태 코드를 나타내는 문자열(예: "400")
     * @param message 사용자에게 표시할 한글 오류 메시지
     */
    ExceptionCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 열거된 상태 코드를 파싱한다.
     *
     * @return `code` 필드의 문자열을 정수로 변환한 값
     */
    public Integer returnCode() {
        return Integer.parseInt(code);
    }
}