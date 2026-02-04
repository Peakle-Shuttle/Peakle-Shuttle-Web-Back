package com.peakle.shuttle.admin.course;

import com.peakle.shuttle.admin.course.dto.*;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.course.entity.Course;
import com.peakle.shuttle.course.entity.Dispatch;
import com.peakle.shuttle.course.entity.Stop;
import com.peakle.shuttle.course.repository.CourseRepository;
import com.peakle.shuttle.course.repository.DispatchRepository;
import com.peakle.shuttle.course.repository.StopRepository;
import com.peakle.shuttle.global.enums.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCourseService {

    private final CourseRepository courseRepository;
    private final StopRepository stopRepository;
    private final DispatchRepository dispatchRepository;

    @Transactional
    public AdminCourseResponse createCourse(CourseCreateRequest request) {
        Course course = Course.builder()
                .courseName(request.courseName())
                .courseSeats(request.courseSeats())
                .courseDuration(request.courseDuration())
                .courseCost(request.courseCost())
                .build();

        courseRepository.save(course);

        if (request.stops() != null) {
            for (CourseStopRequest stopReq : request.stops()) {
                Stop stop = resolveStop(stopReq);
                course.addCourseStop(stop, stopReq.stopOrder(), stopReq.estimatedArrival());
            }
        }

        return AdminCourseResponse.from(course);
    }

    public List<AdminCourseResponse> getCourses() {
        return courseRepository.findAllWithStops().stream()
                .map(AdminCourseResponse::from)
                .toList();
    }

    @Transactional
    public void deleteCourse(Long courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_COURSE));
        courseRepository.delete(course);
    }

    @Transactional
    public void updateCourse(Long courseCode, CourseUpdateRequest request) {
        Course course = courseRepository.findWithStopsByCourseId(courseCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_COURSE));

        if (request.courseName() != null) course.updateCourseName(request.courseName());
        if (request.courseSeats() != null) course.updateCourseSeats(request.courseSeats());
        if (request.courseDuration() != null) course.updateCourseDuration(request.courseDuration());
        if (request.courseCost() != null) course.updateCourseCost(request.courseCost());

        if (request.stops() != null) {
            course.clearCourseStops();
            for (CourseStopRequest stopReq : request.stops()) {
                Stop stop = resolveStop(stopReq);
                course.addCourseStop(stop, stopReq.stopOrder(), stopReq.estimatedArrival());
            }
        }
    }

    @Transactional
    public AdminDispatchResponse createDispatch(DispatchCreateRequest request) {
        Course course = courseRepository.findByCourseCode(request.courseCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_COURSE));

        Dispatch dispatch = Dispatch.builder()
                .course(course)
                .dispatchStartTime(request.dispatchStartTime())
                .dispatchDay(request.dispatchDay())
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
        dispatchRepository.delete(dispatch);
    }

    @Transactional
    public void updateDispatch(DispatchUpdateRequest request) {
        Dispatch dispatch = dispatchRepository.findByDispatchCode(request.dispatchCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_DISPATCH));

        if (request.dispatchStartTime() != null) dispatch.updateStartTime(request.dispatchStartTime());
        if (request.dispatchDay() != null) dispatch.updateDay(request.dispatchDay());
    }

    private Stop resolveStop(CourseStopRequest stopReq) {
        if (stopReq.stopCode() != null) {
            return stopRepository.findByStopCode(stopReq.stopCode())
                    .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_STOP));
        }
        Stop stop = Stop.builder()
                .stopName(stopReq.stopName())
                .stopAddress(stopReq.stopAddress())
                .build();
        return stopRepository.save(stop);
    }
}
