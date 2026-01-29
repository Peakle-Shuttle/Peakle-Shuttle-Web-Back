package com.peakle.shuttle.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** 회원가입 요청 DTO */
@Getter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String userPassword;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String userEmail;

    @Size(max = 20, message = "이름은 20자 이하로 입력해주세요.")
    private String userName;

    @Size(max = 10, message = "성별은 10자 이하로 입력해주세요.")
    private String userGender;

    @Size(max = 20, message = "핸드폰번호는 20자 이하로 입력해주세요.")
    private String userNumber;

    private LocalDate userBirth;

    @Size(max = 100, message = "학교명은 100자 이하로 입력해주세요.")
    private String userSchool;

    @Size(max = 100, message = "전공은 100자 이하로 입력해주세요.")
    private String userMajor;
}
