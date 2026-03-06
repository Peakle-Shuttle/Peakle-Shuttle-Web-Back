package com.peakle.shuttle.course.scheduler;

import com.peakle.shuttle.course.entity.Dispatch;
import com.peakle.shuttle.course.repository.DispatchRepository;
import com.peakle.shuttle.global.enums.DispatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DispatchScheduler {

    private final DispatchRepository dispatchRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void completeExpiredDispatches() {
        List<Dispatch> expired = dispatchRepository
                .findAllByDispatchStatusAndDispatchDatetimeBefore(DispatchStatus.ENABLED, LocalDateTime.now());
        expired.forEach(Dispatch::complete);
        if (!expired.isEmpty()) {
            log.info("완료 처리된 배차 수: {}", expired.size());
        }
    }
}
