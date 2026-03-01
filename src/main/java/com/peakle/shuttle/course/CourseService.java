package com.peakle.shuttle.course;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.course.dto.request.*;
import com.peakle.shuttle.course.dto.response.*;
import com.peakle.shuttle.course.entity.Course;
import com.peakle.shuttle.course.entity.Dispatch;
import com.peakle.shuttle.course.entity.Wish;
import com.peakle.shuttle.course.repository.CourseRepository;
import com.peakle.shuttle.course.repository.DispatchRepository;
import com.peakle.shuttle.course.repository.WishRepository;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final DispatchRepository dispatchRepository;
    private final WishRepository wishRepository;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;

    /**
     * 모든 노선과 배차 정보를 조회합니다.
     *
     * @param userCode 인증된 사용자 코드 (비로그인 시 null)
     * @return 노선 목록
     */
    public List<CourseListResponse> getAllCoursesWithDispatches(Long userCode) {
        List<Course> courses = courseRepository.findAllWithDispatchesAndStops();
        Set<Long> wishedSet = getWishedCourseCodeSet(courses, userCode);
        return courses.stream()
                .map(c -> CourseListResponse.from(c, wishedSet.contains(c.getCourseCode())))
                .toList();
    }

    /**
     * 특정 학교의 노선과 배차 정보를 조회합니다.
     *
     * @param schoolCode 학교 코드
     * @param userCode 인증된 사용자 코드 (비로그인 시 null)
     * @return 해당 학교의 노선 목록
     */
    public List<CourseListResponse> getCoursesBySchool(Long schoolCode, Long userCode) {
        List<Course> courses = courseRepository.findAllBySchoolCodeWithDispatchesAndStops(schoolCode);
        Set<Long> wishedSet = getWishedCourseCodeSet(courses, userCode);
        return courses.stream()
                .map(c -> CourseListResponse.from(c, wishedSet.contains(c.getCourseCode())))
                .toList();
    }

    /**
     * 모든 학교별 노선 정보를 일괄 조회합니다.
     *
     * @param userCode 인증된 사용자 코드 (비로그인 시 null)
     * @return 학교별 노선 목록
     */
    public List<SchoolWithCoursesResponse> getAllSchoolsWithCourses(Long userCode) {
        return schoolRepository.findAll().stream()
                .map(school -> {
                    List<CourseListResponse> courses = getCoursesBySchool(school.getSchoolCode(), userCode);
                    return SchoolWithCoursesResponse.of(school, courses);
                })
                .toList();
    }

    /**
     * 특정 노선의 상세 정보를 조회합니다.
     *
     * @param courseCode 노선 코드
     * @param userCode 인증된 사용자 코드 (비로그인 시 null)
     * @return 노선 상세 정보
     * @throws AuthException 노선을 찾을 수 없는 경우
     */
    public CourseDetailResponse getCourseDetail(Long courseCode, Long userCode) {
        Course course = courseRepository.findWithStopsByCourseId(courseCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_COURSE));
        boolean wished = userCode != null
                && wishRepository.existsByCourseCourseCodeAndUserUserCode(courseCode, userCode);
        return CourseDetailResponse.from(course, wished);
    }

    /**
     * 특정 배차의 상세 정보를 조회합니다.
     *
     * @param dispatchCode 배차 코드
     * @return 배차 상세 정보
     * @throws AuthException 배차를 찾을 수 없는 경우
     */
    public DispatchDetailResponse getDispatchDetail(Long dispatchCode) {
        Dispatch dispatch = dispatchRepository.findByDispatchCodeWithCourseAndStops(dispatchCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_DISPATCH));
        return DispatchDetailResponse.from(dispatch);
    }

    /**
     * 노선 즐겨찾기를 토글합니다. 이미 즐겨찾기된 경우 삭제, 아니면 추가합니다.
     *
     * @param userCode 사용자 코드
     * @param request 즐겨찾기 요청 정보
     * @throws AuthException 사용자 또는 노선을 찾을 수 없는 경우
     */
    @Transactional
    public void toggleWish(Long userCode, WishCreateRequest request) {
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));
        Course course = courseRepository.findByCourseCode(request.courseCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_COURSE));

        wishRepository.findByCourseCourseCodeAndUserUserCode(request.courseCode(), userCode)
                .ifPresentOrElse(
                        wishRepository::delete,
                        () -> {
                            Wish wish = Wish.builder()
                                    .course(course)
                                    .user(user)
                                    .build();
                            wishRepository.save(wish);
                        }
                );
    }

    private Set<Long> getWishedCourseCodeSet(List<Course> courses, Long userCode) {
        if (userCode == null || courses.isEmpty()) return Collections.emptySet();
        List<Long> courseCodes = courses.stream().map(Course::getCourseCode).toList();
        return new HashSet<>(wishRepository.findWishedCourseCodes(courseCodes, userCode));
    }
}
