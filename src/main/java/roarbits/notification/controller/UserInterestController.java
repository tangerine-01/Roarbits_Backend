package roarbits.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;
import roarbits.notification.dto.UserInterestResponseDto;
import roarbits.notification.service.UserInterestService;

import java.util.List;

@Tag(name = "User Interest", description = "과목 즐겨찾기 API")
@RestController
@RequestMapping("/api/interest")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")

public class UserInterestController {

    private final UserInterestService service;

    // 관심 알림 설정 등록 또는 수정
    @PostMapping("/api/interest/subjects/{subjectId}")
    @Operation(
            summary = "관심 과목 등록/수정",
            security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<UserInterestResponseDto> upsertSubjectInterest(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long subjectId
    ) {
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        return ResponseEntity.ok(service.upsertSubjectInterest(userId, subjectId));
    }

    // 사용자 관심 목록 조회
    @GetMapping("/me")
    @Operation(
            summary = "사용자 관심 목록 조회",
            description = "현재 로그인된 사용자의 즐겨찾기 목록을 조회합니다.",
            security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<List<Long>> myInterests(
            @AuthenticationPrincipal(expression = "id") Long userId){
        return ResponseEntity.ok(service.myInterestSubjectIds(userId));
    }

    // 관심 알림 삭제
    @DeleteMapping("/subjects/{subjectId}")
    @Operation(
            summary = "즐겨찾기 해제",
            description = "사용자의 즐겨찾기 설정을 해제합니다. ",
            security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<Void> deleteInterest(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long subjectId)
    {
        service.deleteInterest(userId, subjectId);
        return ResponseEntity.noContent().build();
    }
}