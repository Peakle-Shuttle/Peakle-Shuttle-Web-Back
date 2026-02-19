package com.peakle.shuttle.admin.utm;

import com.peakle.shuttle.admin.utm.dto.request.UtmTrackRequest;
import com.peakle.shuttle.admin.utm.dto.response.UtmStatResponse;
import com.peakle.shuttle.admin.utm.entity.UtmStat;
import com.peakle.shuttle.admin.utm.repository.UtmStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UtmStatService {

    private final UtmStatRepository utmStatRepository;

    /**
     * UTM 추적 기록 (카운트 증가 또는 신규 생성)
     */
    @Transactional
    public void trackUtm(UtmTrackRequest request) {
        utmStatRepository.findByUtmSourceAndUtmMediumAndUtmCampaign(
                request.utmSource(),
                request.utmMedium(),
                request.utmCampaign()
        ).ifPresentOrElse(
                UtmStat::incrementCount,  // 존재하면 카운트 증가
                () -> {                    // 존재하지 않으면 신규 생성
                    UtmStat newStat = UtmStat.builder()
                            .utmSource(request.utmSource())
                            .utmMedium(request.utmMedium())
                            .utmCampaign(request.utmCampaign())
                            .utmCount(1L)
                            .build();
                    utmStatRepository.save(newStat);
                }
        );

        log.info("UTM 추적 기록: source={}, medium={}, campaign={}",
                request.utmSource(), request.utmMedium(), request.utmCampaign());
    }

    /**
     * 전체 UTM 통계 조회 (카운트 내림차순)
     */
    public List<UtmStatResponse> getAllUtmStats() {
        return utmStatRepository.findAllByOrderByUtmCountDesc()
                .stream()
                .map(UtmStatResponse::from)
                .toList();
    }

    /**
     * 특정 소스의 UTM 통계 조회
     */
    public List<UtmStatResponse> getUtmStatsBySource(String source) {
        return utmStatRepository.findByUtmSourceOrderByUtmCountDesc(source)
                .stream()
                .map(UtmStatResponse::from)
                .toList();
    }

    /**
     * 특정 UTM 조합의 상세 정보 조회
     */
    public UtmStatResponse getUtmStat(String source, String medium, String campaign) {
        return utmStatRepository.findByUtmSourceAndUtmMediumAndUtmCampaign(source, medium, campaign)
                .map(UtmStatResponse::from)
                .orElse(null);
    }
}
