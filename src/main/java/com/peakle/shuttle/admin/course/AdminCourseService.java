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

    /**
     * 새 노선을 등록합니다. 정류장 정보가 포함된 경우 함께 등록합니다.
     *
     * @param request 노선 생성 요청 정보
     * @return 등록된 노선 정보
     */
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

    /**
     * 전체 노선 목록을 조회합니다.
     *
     * @return 노선 목록
     */
    public List<AdminCourseResponse> getCourses() {
        return courseRepository.findAllWithStops().stream()
                .map(AdminCourseResponse::from)
                .toList();
    }

    /**
     * 노선을 삭제합니다.
     *
     * @param courseCode 삭제할 노선 코드
     * @throws AuthException 노선을 찾을 수 없는 경우
     */
    @Transactional
    public void deleteCourse(Long courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_COURSE));
        courseRepository.delete(course);
    }

    /**
     * 노선 정보를 수정합니다. null이 아닌 필드만 업데이트됩니다.
     *
     * @param courseCode 수정할 노선 코드
     * @param request 노선 수정 요청 정보
     * @throws AuthException 노선을 찾을 수 없는 경우
     */
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

    /**
     * 새 배차를 등록합니다.
     *
     * @param request 배차 생성 요청 정보
     * @return 등록된 배차 정보
     * @throws AuthException 노선을 찾을 수 없는 경우
     */
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

    /**
     * 특정 노선의 배차 목록을 조회합니다.
     *
     * @param courseCode 노선 코드
     * @return 배차 목록
     */
    public List<AdminDispatchResponse> getDispatches(Long courseCode) {
        return dispatchRepository.findAllByCourseCourseCode(courseCode).stream()
                .map(AdminDispatchResponse::from)
                .toList();
    }

    /**
     * 배차를 삭제합니다.
     *
     * @param dispatchCode 삭제할 배차 코드
     * @throws AuthException 배차를 찾을 수 없는 경우
     */
    @Transactional
    public void deleteDispatch(Long dispatchCode) {
        Dispatch dispatch = dispatchRepository.findByDispatchCode(dispatchCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_DISPATCH));
        dispatchRepository.delete(dispatch);
    }

    /**
     * 배차 정보를 수정합니다.
     *
     * @param request 배차 수정 요청 정보
     * @throws AuthException 배차를 찾을 수 없는 경우
     */
    @Transactional
    public void updateDispatch(DispatchUpdateRequest request) {
        Dispatch dispatch = dispatchRepository.findByDispatchCode(request.dispatchCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_DISPATCH));

        if (request.dispatchStartTime() != null) dispatch.updateStartTime(request.dispatchStartTime());
        if (request.dispatchDay() != null) dispatch.updateDay(request.dispatchDay());
    }

    /**
     * 정류장을 조회하거나 새로 생성합니다.
     *
     * @param stopReq 정류장 요청 정보
     * @return Stop 엔티티
     */
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
