package com.peakle.shuttle.admin.matching;

import com.peakle.shuttle.admin.matching.dto.request.AdminMatchingStatusUpdateRequest;
import com.peakle.shuttle.admin.matching.dto.response.AdminMatchingResponse;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.global.enums.MatchingStatus;
import com.peakle.shuttle.matching.entity.Matching;
import com.peakle.shuttle.matching.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMatchingService {

    private final MatchingRepository matchingRepository;

    public List<AdminMatchingResponse> getAllMatchings() {
        return matchingRepository.findAllByStatusNot(MatchingStatus.DELETED).stream()
                .map(AdminMatchingResponse::from)
                .toList();
    }

    public AdminMatchingResponse getMatching(Long matchingCode) {
        Matching matching = matchingRepository.findByMatchingCodeWithUser(matchingCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_MATCHING));
        return AdminMatchingResponse.from(matching);
    }

    @Transactional
    public void updateStatus(Long matchingCode, AdminMatchingStatusUpdateRequest request) {
        Matching matching = matchingRepository.findById(matchingCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_MATCHING));

        switch (request.status()) {
            case PROCESSING -> matching.process();
            case COMPLETED -> matching.complete();
            default -> throw new IllegalArgumentException("Invalid status: " + request.status());
        }
    }

    @Transactional
    public void deleteMatching(Long matchingCode) {
        Matching matching = matchingRepository.findById(matchingCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_MATCHING));
        matching.softDelete();
    }
}
