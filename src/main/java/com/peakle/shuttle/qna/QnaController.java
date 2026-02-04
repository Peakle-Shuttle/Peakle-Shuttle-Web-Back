package com.peakle.shuttle.qna;

import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import com.peakle.shuttle.qna.dto.QnaCreateRequest;
import com.peakle.shuttle.qna.dto.QnaListResponse;
import com.peakle.shuttle.qna.dto.QnaUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qna")
@RequiredArgsConstructor
@Tag(name = "QNA", description = "1:1 문의 API")
public class QnaController {

    private final QnaService qnaService;

    /**
     * 내 1:1 문의 목록을 조회합니다.
     *
     * @param user 인증된 사용자 정보
     * @return 문의 목록
     */
    @Operation(summary = "1:1 문의 목록 조회", description = "내 1:1 문의 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<QnaListResponse>> getMyQnas(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(qnaService.getMyQnas(user.code()));
    }

    /**
     * 1:1 문의를 작성합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 문의 작성 요청 정보
     */
    @Operation(summary = "1:1 문의 작성", description = "1:1 문의를 작성합니다.")
    @PostMapping
    public ResponseEntity<Void> createQna(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody QnaCreateRequest request
    ) {
        qnaService.createQna(user.code(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 1:1 문의를 수정합니다.
     *
     * @param user 인증된 사용자 정보
     * @param request 문의 수정 요청 정보
     */
    @Operation(summary = "1:1 문의 수정", description = "1:1 문의를 수정합니다.")
    @PatchMapping
    public ResponseEntity<Void> updateQna(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody QnaUpdateRequest request
    ) {
        qnaService.updateQna(user.code(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 1:1 문의를 삭제합니다.
     *
     * @param user 인증된 사용자 정보
     * @param qnaCode 문의 코드
     */
    @Operation(summary = "1:1 문의 삭제", description = "1:1 문의를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteQna(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @RequestParam Long qnaCode
    ) {
        qnaService.deleteQna(user.code(), qnaCode);
        return ResponseEntity.noContent().build();
    }
}
