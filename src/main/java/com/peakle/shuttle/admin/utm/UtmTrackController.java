package com.peakle.shuttle.admin.utm;

import com.peakle.shuttle.admin.utm.dto.request.UtmTrackRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/utm")
@RequiredArgsConstructor
@Tag(name = "UTM", description = "UTM 추적 API")
public class UtmTrackController {

    private final UtmStatService utmStatService;

    @Operation(summary = "UTM 추적", description = "UTM 파라미터를 기록하고 카운트를 증가시킵니다.")
    @PostMapping("/")
    public ResponseEntity<Void> trackUtm(
            @Valid @RequestBody UtmTrackRequest request
    ) {
        utmStatService.trackUtm(request);
        return ResponseEntity.ok().build();
    }
}
