package com.peakle.shuttle.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/** 카카오 회원가입 요청 DTO */
@Getter
@NoArgsConstructor
public class KakaoSignupRequest {

    @NotBlank(message = "카카오 토큰을 입력해주세요.")
    private String providerToken;

    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String userEmail;

    @Size(max = 20, message = "이름은 20자 이하로 입력해주세요.")
    private String userName;

    @Size(max = 10, message = "성별은 10자 이하로 입력해주세요.")
    private String userGender;

    @Size(max = 20, message = "핸드폰번호는 20자 이하로 입력해주세요.")
    private String userNumber;

    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate userBirth;

    private Long schoolCode;

    @Size(max = 100, message = "전공은 100자 이하로 입력해주세요.")
    private String userMajor;
}
