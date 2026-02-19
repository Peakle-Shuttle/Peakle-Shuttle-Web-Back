package com.peakle.shuttle.auth.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** 회원 정보 수정 요청 DTO (핸드폰, 학교, 전공, 생년월일) */
public record UserInfoRequest(
    @Size(max = 20, message = "핸드폰번호는 20자 이하로 입력해주세요.")
    String userNumber,

    Long schoolCode,

    @Size(max = 100, message = "전공은 100자 이하로 입력해주세요.")
    String userMajor,

    LocalDate userBirth
) {}
