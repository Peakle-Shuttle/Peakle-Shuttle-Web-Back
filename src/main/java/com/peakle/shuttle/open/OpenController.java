package com.peakle.shuttle.open;

import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import com.peakle.shuttle.open.dto.request.OpenCreateRequest;
import com.peakle.shuttle.open.dto.request.OpenUpdateRequest;
import com.peakle.shuttle.open.dto.request.OpenWishRequest;
import com.peakle.shuttle.open.dto.response.OpenListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
@Tag(name = "Open", description = "셔틀 개설 요청 API")
public class OpenController {

    private final OpenService openService;

    /**
     * 셔틀 개설 요청 목록을 조회합니다.
     *
     * @param user 인증된 사용자 정보
     * @return 개설 요청 목록
     */
    @Operation(summary = "개설 요청 목록 조회", description = "셔틀 개설 요청 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<OpenListResponse>> getOpens(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(openService.getAllOpens());
    }

    /**
     * 내 셔틀 개설 요청 목록을 조회합니다.
     *
     * @param user 인증된 사용자 정보
     * @return 개설 요청 목록
     */
    @Operation(summary = "내 개설 요청 조회", description = "내 셔틀 개설 요청 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<List<OpenListResponse>> getMyOpens(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(openService.getMyOpens(user.code()));
    }

    /**
     * 셔틀 개설 요청을 작성합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 개설 요청 작성 정보
     */
    @Operation(summary = "개설 요청 작성", description = "셔틀 개설 요청을 작성합니다.")
    @PostMapping
    public ResponseEntity<Void> createOpen(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody OpenCreateRequest request
    ) {
        openService.createOpen(user.code(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 셔틀 개설 요청을 수정합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 개설 요청 수정 정보
     */
    @Operation(summary = "개설 요청 수정", description = "셔틀 개설 요청을 수정합니다.")
    @PatchMapping
    public ResponseEntity<Void> updateOpen(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody OpenUpdateRequest request
    ) {
        openService.updateOpen(user.code(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 셔틀 개설 요청을 삭제합니다.
     *
     * @param user 인증된 사용자 정보
     * @param openCode 개설 요청 코드
     */
    @Operation(summary = "개설 요청 삭제", description = "셔틀 개설 요청을 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteOpen(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam Long openCode
    ) {
        openService.deleteOpen(user.code(), openCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * 개설 요청 좋아요를 추가하거나 제거합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 좋아요 요청 정보
     */
    @Operation(summary = "개설 요청 좋아요", description = "셔틀 개설 요청에 좋아요를 추가하거나 제거합니다.")
    @PostMapping("/wish")
    public ResponseEntity<Void> toggleOpenWish(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody OpenWishRequest request
    ) {
        openService.toggleOpenWish(user.code(), request);
        return ResponseEntity.noContent().build();
    }
}
