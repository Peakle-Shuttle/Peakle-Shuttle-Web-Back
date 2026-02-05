package com.peakle.shuttle.school.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SchoolCreateRequest(
    @NotBlank(message = "학교명을 입력해주세요.")
    @Size(max = 100, message = "학교명은 100자 이하로 입력해주세요.")
    String schoolName,

    @Size(max = 200, message = "주소는 200자 이하로 입력해주세요.")
    String schoolAddress
) {}
