package com.peakle.shuttle.matching;

import com.peakle.shuttle.auth.entity.User;
import com.peakle.shuttle.auth.repository.UserRepository;
import com.peakle.shuttle.core.exception.extend.AuthException;
import com.peakle.shuttle.global.enums.ExceptionCode;
import com.peakle.shuttle.matching.dto.request.MatchingCreateRequest;
import com.peakle.shuttle.matching.dto.request.MatchingUpdateRequest;
import com.peakle.shuttle.matching.dto.response.MatchingResponse;
import com.peakle.shuttle.matching.entity.Matching;
import com.peakle.shuttle.matching.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchingService {

    private final MatchingRepository matchingRepository;
    private final UserRepository userRepository;

    @Transactional
    public MatchingResponse createMatching(Long userCode, MatchingCreateRequest request) {
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_USER));

        Matching matching = Matching.builder()
                .user(user)
                .customerType(request.customerType())
                .organizationName(request.organizationName())
                .managerName(request.managerName())
                .email(request.email())
                .contact(request.contact())
                .purpose(request.purpose())
                .departureDateTime(request.departureDateTime())
                .departure(request.departure())
                .destination(request.destination())
                .estimatedPassengers(request.estimatedPassengers())
                .vehicleType(request.vehicleType())
                .operationType(request.operationType())
                .requiredDocuments(request.requiredDocuments())
                .additionalRequests(request.additionalRequests())
                .privacyConsent(request.privacyConsent())
                .build();

        matchingRepository.save(matching);
        return MatchingResponse.from(matching);
    }

    public MatchingResponse getMatching(Long matchingCode) {
        Matching matching = matchingRepository.findById(matchingCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_MATCHING));
        return MatchingResponse.from(matching);
    }

    public List<MatchingResponse> getMatchings(Long userCode) {
        return matchingRepository.findAllByUser_UserCode(userCode).stream()
                .map(MatchingResponse::from)
                .toList();
    }

    @Transactional
    public void updateMatching(Long matchingCode, MatchingUpdateRequest request) {
        Matching matching = matchingRepository.findById(matchingCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_MATCHING));

        matching.update(
                request.customerType(),
                request.organizationName(),
                request.managerName(),
                request.email(),
                request.contact(),
                request.purpose(),
                request.departureDateTime(),
                request.departure(),
                request.destination(),
                request.estimatedPassengers(),
                request.vehicleType(),
                request.operationType(),
                request.requiredDocuments(),
                request.additionalRequests(),
                request.privacyConsent()
        );
    }

    @Transactional
    public void deleteMatching(Long matchingCode) {
        Matching matching = matchingRepository.findById(matchingCode)
                .orElseThrow(() -> new AuthException(ExceptionCode.NOT_FOUND_MATCHING));
        matchingRepository.delete(matching);
    }
}
