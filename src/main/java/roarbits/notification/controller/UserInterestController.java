package roarbits.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import roarbits.notification.service.UserInterestService;

import java.util.List;

@Tag(name = "User Interest", description = "과목 즐겨찾기 API")
@RestController
@RequestMapping("/api/interest")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")

public class UserInterestController {

    private final UserInterestService service;

    @PostMapping("/subjects/{subjectId}")
    @Operation(
            summary = "관심 알림 설정 등록 또는 수정",
            description = "사용자의 관심 알림 설정을 등록하거나 수정합니다. 이미 존재하는 경우에는 아무 작업도 하지 않습니다.")
    public ResponseEntity<Void> add(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long subjectId) {
        service.saveOrUpdateInterest(userId, subjectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(
            summary = "사용자 관심 목록 조회",
            description = "현재 로그인된 사용자의 즐겨찾기 목록을 조회합니다.")
    public ResponseEntity<List<Long>> myInterests(
            @AuthenticationPrincipal(expression = "id") Long userId){
        return ResponseEntity.ok(service.myInterestSubjectIds(userId));
    }

    @DeleteMapping("/subjects/{subjectId}")
    @Operation(
            summary = "즐겨찾기 해제",
            description = "사용자의 즐겨찾기 설정을 해제합니다. ")
    public ResponseEntity<Void> deleteInterest(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long subjectId)
    {
        service.deleteInterest(userId, subjectId);
        return ResponseEntity.noContent().build();
    }
}