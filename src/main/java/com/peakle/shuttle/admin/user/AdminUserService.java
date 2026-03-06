package com.peakle.shuttle.admin.user;

import com.peakle.shuttle.admin.user.dto.response.AdminUserListResponse;
import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.global.enums.UserStatus;
import com.peakle.shuttle.matching.repository.MatchingRepository;
import com.peakle.shuttle.qna.repository.QnaRepository;
import com.peakle.shuttle.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;
    private final QnaRepository qnaRepository;
    private final MatchingRepository matchingRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 활성 사용자 목록을 통계 정보와 함께 조회합니다.
     *
     * @return 사용자 목록 (QnA, 매칭, 예약 통계 포함)
     */
    public List<AdminUserListResponse> getUsers() {
        List<User> users = userRepository.findAllByUserStatus(UserStatus.ACTIVE);

        // 사용자별 QnA 전체/완료 개수 (userCode -> [total, answered])
        Map<Long, long[]> qnaStats = qnaRepository.countByUserGrouped().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> new long[]{(Long) row[1], (Long) row[2]}
                ));

        // 사용자별 매칭 전체/완료 개수 (userCode -> [total, completed])
        Map<Long, long[]> matchingStats = matchingRepository.countByUserGrouped().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> new long[]{(Long) row[1], (Long) row[2]}
                ));

        // 사용자별 예약 횟수 (userCode -> count)
        Map<Long, Long> reservationStats = reservationRepository.findAllUserReservationCounts().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        // 사용자별 총 구매금액 (userCode -> totalAmount)
        Map<Long, Long> purchaseStats = reservationRepository.findAllUserTotalPurchaseAmounts().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        return users.stream()
                .map(user -> {
                    Long userCode = user.getUserCode();
                    long[] qna = qnaStats.getOrDefault(userCode, new long[]{0, 0});
                    long[] matching = matchingStats.getOrDefault(userCode, new long[]{0, 0});
                    Long reservations = reservationStats.getOrDefault(userCode, 0L);
                    Long totalPurchase = purchaseStats.getOrDefault(userCode, 0L);

                    return AdminUserListResponse.of(user, qna[0], qna[1], matching[0], matching[1], reservations, totalPurchase);
                })
                .toList();
    }
}
