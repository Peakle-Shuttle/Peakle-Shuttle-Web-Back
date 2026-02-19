package com.peakle.shuttle.admin.qna;

import com.peakle.shuttle.admin.qna.dto.request.QnaCommentCreateRequest;
import com.peakle.shuttle.admin.qna.dto.response.AdminQnaCommentResponse;
import com.peakle.shuttle.admin.qna.dto.response.AdminQnaDetailResponse;
import com.peakle.shuttle.admin.qna.dto.response.AdminQnaListResponse;
import com.peakle.shuttle.auth.dto.request.AuthUserRequest;
import com.peakle.shuttle.core.annotation.SignUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/qna")
@RequiredArgsConstructor
@Tag(name = "Admin QnA", description = "관리자 1:1 문의 관리 API")
public class AdminQnaController {

    private final AdminQnaService adminQnaService;

    /**
     * 1:1 문의 목록을 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @return 문의 목록
     */
    @Operation(summary = "문의 목록 조회", description = "1:1 문의 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AdminQnaListResponse>> getQnaList(
            @Parameter(hidden = true) @SignUser AuthUserRequest user
    ) {
        return ResponseEntity.ok(adminQnaService.getQnaList());
    }

    /**
     * 1:1 문의 상세 내용과 답변을 조회합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param qnaId 문의 ID
     * @return 문의 상세 정보
     */
    @Operation(summary = "문의 상세 조회", description = "1:1 문의 상세 내용과 답변을 조회합니다.")
    @GetMapping("/{qnaId}")
    public ResponseEntity<AdminQnaDetailResponse> getQnaDetail(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @PathVariable Long qnaId
    ) {
        return ResponseEntity.ok(adminQnaService.getQnaDetail(qnaId));
    }

    /**
     * 1:1 문의에 답변을 등록합니다.
     *
     * @param user 인증된 관리자 사용자 정보
     * @param request 답변 생성 요청 정보
     * @return 등록된 답변 정보
     */
    @Operation(summary = "문의 답변 등록", description = "1:1 문의에 답변을 등록합니다.")
    @PostMapping("/comment")
    public ResponseEntity<AdminQnaCommentResponse> createComment(
            @Parameter(hidden = true) @SignUser AuthUserRequest user,
            @Valid @RequestBody QnaCommentCreateRequest request
    ) {
        return ResponseEntity.ok(adminQnaService.createComment(user.code(), request));
    }
}
