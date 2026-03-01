package com.peakle.shuttle.course.dto.response;

// import com.peakle.shuttle.course.entity.CourseStop;
import com.peakle.shuttle.course.entity.Dispatch;

// import java.util.List;
import java.time.LocalDateTime;

public record DispatchDetailResponse(
        Long dispatchCode,
        Long courseCode,
        String courseName,
        LocalDateTime dispatchDatetime,
        Integer totalSeats,
        Integer occupiedSeats,
        Integer availableSeats,
        Integer courseCost,
        String departureStopName,
        String arrivalStopName
        // List<StopInfo> stops
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

    public static DispatchDetailResponse from(Dispatch dispatch) {
        Integer totalSeats = dispatch.getCourse().getCourseSeats();
        Integer occupied = dispatch.getDispatchOccupied() != null ? dispatch.getDispatchOccupied() : 0;

        // 추후 stops 목록 반환 시 아래 주석 해제
        // List<StopInfo> stopInfos = dispatch.getCourse().getCourseStops().stream()
        //         .map(StopInfo::from)
        //         .toList();

        String departure = dispatch.getCourse().getDepartureStop() != null ? dispatch.getCourse().getDepartureStop().getStopName() : null;
        String arrival = dispatch.getCourse().getArrivalStop() != null ? dispatch.getCourse().getArrivalStop().getStopName() : null;

        return new DispatchDetailResponse(
                dispatch.getDispatchCode(),
                dispatch.getCourse().getCourseCode(),
                dispatch.getCourse().getCourseName(),
                dispatch.getDispatchDatetime(),
                totalSeats,
                occupied,
                totalSeats - occupied,
                dispatch.getCourse().getCourseCost(),
                departure,
                arrival
                // stopInfos
        );
    }
}
