package com.peakle.shuttle.admin.course;

import com.peakle.shuttle.admin.course.dto.request.*;
import com.peakle.shuttle.admin.course.dto.response.*;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.course.entity.Course;
import com.peakle.shuttle.course.entity.Dispatch;
import com.peakle.shuttle.course.repository.CourseRepository;
import com.peakle.shuttle.course.repository.DispatchRepository;
import com.peakle.shuttle.global.enums.CourseStatus;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.school.entity.School;
import com.peakle.shuttle.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCourseService {

    private final CourseRepository courseRepository;
    private final DispatchRepository dispatchRepository;
    private final SchoolRepository schoolRepository;

    @Transactional
    public AdminCourseResponse createCourse(CourseCreateRequest request) {
        School school = schoolRepository.findBySchoolCode(request.schoolCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_SCHOOL));

        Course course = Course.builder()
                .school(school)
                .courseName(request.courseName())
                .courseSeats(request.courseSeats())
                .courseDuration(request.courseDuration())
                .courseCost(request.courseCost())
                .departureName(request.departureName())
                .departureAddress(request.departureAddress())
                .arrivalName(request.arrivalName())
                .arrivalAddress(request.arrivalAddress())
                .build();

        courseRepository.save(course);
        return AdminCourseResponse.from(course);
    }

    public List<AdminCourseResponse> getCourses() {
        return courseRepository.findAllWithDispatchesAndStops().stream()
                .map(AdminCourseResponse::from)
                .toList();
    }

    @Transactional
    public void deleteCourse(Long courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_COURSE));
        course.disable();
    }

    @Transactional
    public void enableCourse(Long courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_COURSE));
        course.enable();
    }

    @Transactional
    public void updateCourse(Long courseCode, CourseUpdateRequest request) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_COURSE));

        if (request.courseName() != null) course.updateCourseName(request.courseName());
        if (request.courseSeats() != null) course.updateCourseSeats(request.courseSeats());
        if (request.courseDuration() != null) course.updateCourseDuration(request.courseDuration());
        if (request.courseCost() != null) course.updateCourseCost(request.courseCost());
        if (request.departureName() != null) course.updateDepartureName(request.departureName());
        if (request.departureAddress() != null) course.updateDepartureAddress(request.departureAddress());
        if (request.arrivalName() != null) course.updateArrivalName(request.arrivalName());
        if (request.arrivalAddress() != null) course.updateArrivalAddress(request.arrivalAddress());
    }

    @Transactional
    public AdminDispatchResponse createDispatch(DispatchCreateRequest request) {
        Course course = courseRepository.findByCourseCode(request.courseCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_COURSE));
        if (course.getCourseStatus() == CourseStatus.DISABLE) {
            throw new AuthException(ExceptionCode.DISABLED_COURSE);
        }

        Dispatch dispatch = Dispatch.builder()
                .course(course)
                .dispatchDatetime(request.dispatchDatetime())
                .build();

        dispatchRepository.save(dispatch);
        return AdminDispatchResponse.from(dispatch);
    }

    public List<AdminDispatchResponse> getDispatches(Long courseCode) {
        return dispatchRepository.findAllByCourseCourseCode(courseCode).stream()
                .map(AdminDispatchResponse::from)
                .toList();
    }

    @Transactional
    public void deleteDispatch(Long dispatchCode) {
        Dispatch dispatch = dispatchRepository.findByDispatchCode(dispatchCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_DISPATCH));
        dispatch.cancel();
    }

    @Transactional
    public void updateDispatch(DispatchUpdateRequest request) {
        Dispatch dispatch = dispatchRepository.findByDispatchCode(request.dispatchCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_DISPATCH));

        if (request.dispatchDatetime() != null) dispatch.updateDatetime(request.dispatchDatetime());
    }
}
