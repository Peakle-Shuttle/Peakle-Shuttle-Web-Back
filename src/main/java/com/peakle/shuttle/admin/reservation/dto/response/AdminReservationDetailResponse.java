package com.peakle.shuttle.admin.reservation.dto.response;

import com.peakle.shuttle.auth.dto.response.UserClientResponse;
import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.reservation.dto.response.ReservationResponse;
import com.peakle.shuttle.reservation.entity.Reservation;

public record AdminReservationDetailResponse(
        ReservationResponse reservation,
        UserClientResponse user
) {
    public static AdminReservationDetailResponse from(Reservation reservation) {
        User user = reservation.getUser();

        ReservationResponse reservationInfo = ReservationResponse.from(reservation);

        UserClientResponse userInfo = UserClientResponse.builder()
                .userCode(user.getUserCode())
                .userId(user.getUserId())
                .userEmail(user.getUserEmail())
                .userName(user.getUserName())
                .userRole(user.getUserRole())
                .userGender(user.getUserGender())
                .userNumber(user.getUserNumber())
                .userBirth(user.getUserBirth())
                .schoolCode(user.getSchool() != null ? user.getSchool().getSchoolCode() : null)
                .schoolName(user.getSchool() != null ? user.getSchool().getSchoolName() : null)
                .userMajor(user.getUserMajor())
                .userAddress(user.getUserAddress())
                .userDetailAddress(user.getUserDetailAddress())
                .userPostcode(user.getUserPostcode())
                .isAgreedMarketing(user.getIsAgreedMarketing())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        return new AdminReservationDetailResponse(reservationInfo, userInfo);
    }
}
