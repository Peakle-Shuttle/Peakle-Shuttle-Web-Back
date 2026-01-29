package com.peakle.shuttle.auth.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserInfoRequest(
    @Size(max = 20, message = "핸드폰번호는 20자 이하로 입력해주세요.")
    String userNumber,

    @Size(max = 100, message = "학교명은 100자 이하로 입력해주세요.")
    String userSchool,

    @Size(max = 100, message = "전공은 100자 이하로 입력해주세요.")
    String userMajor,

    LocalDate userBirth
) {}
