package com.peakle.shuttle.school;

import com.peakle.shuttle.core.exception.extend.InvalidArgumentException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.school.dto.request.SchoolCreateRequest;
import com.peakle.shuttle.school.dto.request.SchoolUpdateRequest;
import com.peakle.shuttle.school.dto.response.SchoolResponse;
import com.peakle.shuttle.school.entity.School;
import com.peakle.shuttle.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolService {

    private final SchoolRepository schoolRepository;

    /**
     * 모든 학교 목록을 조회합니다.
     */
    public List<SchoolResponse> getAllSchools() {
        return schoolRepository.findAll().stream()
                .map(SchoolResponse::from)
                .toList();
    }

    /**
     * 특정 학교 정보를 조회합니다.
     */
    public SchoolResponse getSchool(Long schoolCode) {
        School school = schoolRepository.findBySchoolCode(schoolCode)
                .orElseThrow(() -> new InvalidArgumentException(ExceptionCode.NOT_FOUND_SCHOOL));
        return SchoolResponse.from(school);
    }

    /**
     * 학교를 생성합니다.
     */
    @Transactional
    public SchoolResponse createSchool(SchoolCreateRequest request) {
        School school = School.builder()
                .schoolName(request.schoolName())
                .schoolAddress(request.schoolAddress())
                .build();
        School savedSchool = schoolRepository.save(school);
        return SchoolResponse.from(savedSchool);
    }

    /**
     * 학교 정보를 수정합니다.
     */
    @Transactional
    public SchoolResponse updateSchool(Long schoolCode, SchoolUpdateRequest request) {
        School school = schoolRepository.findBySchoolCode(schoolCode)
                .orElseThrow(() -> new InvalidArgumentException(ExceptionCode.NOT_FOUND_SCHOOL));

        if (request.schoolName() != null) {
            school.updateSchoolName(request.schoolName());
        }
        if (request.schoolAddress() != null) {
            school.updateSchoolAddress(request.schoolAddress());
        }

        return SchoolResponse.from(school);
    }

    /**
     * 학교를 삭제합니다.
     */
    @Transactional
    public void deleteSchool(Long schoolCode) {
        School school = schoolRepository.findBySchoolCode(schoolCode)
                .orElseThrow(() -> new InvalidArgumentException(ExceptionCode.NOT_FOUND_SCHOOL));
        schoolRepository.delete(school);
    }
}
