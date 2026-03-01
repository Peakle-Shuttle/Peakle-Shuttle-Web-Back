package com.peakle.shuttle.course.dto.response;

import com.peakle.shuttle.course.entity.Course;
// import com.peakle.shuttle.course.entity.CourseStop;
import com.peakle.shuttle.course.entity.Dispatch;

import java.time.LocalDateTime;
import java.util.List;

public record CourseDetailResponse(
        Long courseCode,
        String courseName,
        Integer courseSeats,
        Integer courseDuration,
        Integer courseCost,
        String departureStopName,
        String arrivalStopName,
        // List<StopInfo> stops,
        List<DispatchInfo> dispatches,
        Boolean wished
) {
    // 추후 확장 시 stops 목록 반환에 활용 가능
    // public record StopInfo(
    //         Long stopCode,
    //         String stopName,
    //         String stopAddress,
    //         int stopOrder,
    //         Integer estimatedArrival
    // ) {
    //     public static StopInfo from(CourseStop cs) {
    //         return new StopInfo(
    //                 cs.getStop().getStopCode(),
    //                 cs.getStop().getStopName(),
    //                 cs.getStop().getStopAddress(),
    //                 cs.getStopOrder(),
    //                 cs.getEstimatedArrival()
    //         );
    //     }
    // }

    public record DispatchInfo(
            Long dispatchCode,
            LocalDateTime dispatchDatetime,
            Integer availableSeats
    ) {
        public static DispatchInfo from(Dispatch dispatch, Integer totalSeats) {
            Integer occupied = dispatch.getDispatchOccupied() != null ? dispatch.getDispatchOccupied() : 0;
            return new DispatchInfo(
                    dispatch.getDispatchCode(),
                    dispatch.getDispatchDatetime(),
                    totalSeats - occupied
            );
        }
    }

    public static CourseDetailResponse from(Course course, boolean wished) {
        // 추후 stops 목록 반환 시 아래 주석 해제
        // List<StopInfo> stopInfos = course.getCourseStops().stream()
        //         .map(StopInfo::from)
        //         .toList();

        String departure = course.getDepartureStop() != null ? course.getDepartureStop().getStopName() : null;
        String arrival = course.getArrivalStop() != null ? course.getArrivalStop().getStopName() : null;

        List<DispatchInfo> dispatchInfos = course.getDispatches().stream()
                .map(d -> DispatchInfo.from(d, course.getCourseSeats()))
                .toList();

        return new CourseDetailResponse(
                course.getCourseCode(),
                course.getCourseName(),
                course.getCourseSeats(),
                course.getCourseDuration(),
                course.getCourseCost(),
                departure,
                arrival,
                // stopInfos,
                dispatchInfos,
                wished
        );
    }
}
