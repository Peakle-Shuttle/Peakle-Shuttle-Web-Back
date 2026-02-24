package com.peakle.shuttle.open;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.open.dto.request.OpenCreateRequest;
import com.peakle.shuttle.open.dto.request.OpenUpdateRequest;
import com.peakle.shuttle.open.dto.request.OpenWishRequest;
import com.peakle.shuttle.open.dto.response.OpenListResponse;
import com.peakle.shuttle.open.entity.Open;
import com.peakle.shuttle.open.entity.OpenWish;
import com.peakle.shuttle.open.repository.OpenRepository;
import com.peakle.shuttle.open.repository.OpenWishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpenService {

    private final OpenRepository openRepository;
    private final OpenWishRepository openWishRepository;
    private final UserRepository userRepository;

    /**
     * 전체 셔틀 개설 요청 목록을 조회합니다.
     *
     * @param userCode 사용자 코드 (비로그인 시 null)
     * @return 개설 요청 목록
     */
    public List<OpenListResponse> getAllOpens(Long userCode) {
        List<Open> opens = openRepository.findAllWithUser();
        return toOpenListResponses(opens, userCode);
    }

    /**
     * 내 셔틀 개설 요청 목록을 조회합니다.
     *
     * @param userCode 사용자 코드
     * @return 개설 요청 목록
     */
    public List<OpenListResponse> getMyOpens(Long userCode) {
        List<Open> opens = openRepository.findAllByUserCodeWithUser(userCode);
        return toOpenListResponses(opens, userCode);
    }

    private List<OpenListResponse> toOpenListResponses(List<Open> opens, Long userCode) {
        if (opens.isEmpty()) {
            return List.of();
        }

        List<Long> openCodes = opens.stream()
                .map(Open::getOpenCode)
                .toList();

        Map<Long, Long> wishCountMap = openWishRepository.countByOpenCodes(openCodes).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).longValue()
                ));

        Set<Long> wishedOpenCodes = userCode != null
                ? new HashSet<>(openWishRepository.findWishedOpenCodes(openCodes, userCode))
                : Collections.emptySet();

        return opens.stream()
                .map(open -> OpenListResponse.from(
                        open,
                        wishCountMap.getOrDefault(open.getOpenCode(), 0L),
                        wishedOpenCodes.contains(open.getOpenCode())
                ))
                .toList();
    }

    /**
     * 셔틀 개설 요청을 작성합니다.
     *
     * @param userCode 사용자 코드
     * @param request 개설 요청 작성 정보
     * @throws AuthException 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public void createOpen(Long userCode, OpenCreateRequest request) {
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        Open open = Open.builder()
                .user(user)
                .openContent(request.openContent())
                .build();

        openRepository.save(open);
    }

    /**
     * 셔틀 개설 요청을 수정합니다.
     *
     * @param userCode 사용자 코드
     * @param request 개설 요청 수정 정보
     * @throws AuthException 개설 요청을 찾을 수 없는 경우
     */
    @Transactional
    public void updateOpen(Long userCode, OpenUpdateRequest request) {
        Open open = openRepository.findByOpenCodeAndUserUserCode(request.openCode(), userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_OPEN));

        if (request.openContent() != null) {
            open.updateOpenContent(request.openContent());
        }
    }

    /**
     * 셔틀 개설 요청을 삭제합니다.
     *
     * @param userCode 사용자 코드
     * @param openCode 개설 요청 코드
     * @throws AuthException 개설 요청을 찾을 수 없는 경우
     */
    @Transactional
    public void deleteOpen(Long userCode, Long openCode) {
        Open open = openRepository.findByOpenCodeAndUserUserCode(openCode, userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_OPEN));
        openRepository.delete(open);
    }

    /**
     * 개설 요청 좋아요를 토글합니다. 이미 좋아요한 경우 삭제, 아니면 추가합니다.
     *
     * @param userCode 사용자 코드
     * @param request 좋아요 요청 정보
     * @throws AuthException 사용자 또는 개설 요청을 찾을 수 없는 경우
     */
    @Transactional
    public void toggleOpenWish(Long userCode, OpenWishRequest request) {
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));
        Open open = openRepository.findByOpenCode(request.openCode())
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_OPEN));

        openWishRepository.findByOpenOpenCodeAndUserUserCode(request.openCode(), userCode)
                .ifPresentOrElse(
                        openWishRepository::delete,
                        () -> {
                            OpenWish openWish = OpenWish.builder()
                                    .open(open)
                                    .user(user)
                                    .build();
                            openWishRepository.save(openWish);
                        }
                );
    }
}
