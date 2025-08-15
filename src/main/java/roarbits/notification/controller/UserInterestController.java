package roarbits.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

import roarbits.notification.dto.UserInterestRequestDto;
import roarbits.notification.dto.UserInterestResponseDto;
import roarbits.notification.service.UserInterestService;

@Tag(name = "User Interest", description = "사용자 관심 알림 설정 API")
@RestController
@RequestMapping("/api/interest")
@RequiredArgsConstructor
public class UserInterestController {

    private final UserInterestService userInterestService;

    // 관심 알림 설정 등록 또는 수정
    @PostMapping("/settings")
    @Operation(
            summary = "관심 알림 설정 등록 또는 수정",
            description = "사용자의 관심 알림 설정을 등록하거나 수정합니다. " +
                    "이미 존재하는 관심 알림 설정은 업데이트되며, 새로운 설정은 추가됩니다.",
            security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<UserInterestResponseDto> saveOrUpdateInterest(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "관심 알림 설정 정보")
            @Valid @org.springframework.web.bind.annotation.RequestBody UserInterestRequestDto requestDto
    ) {
        UserInterestResponseDto responseDto = userInterestService.saveOrUpdateInterest(userId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 사용자 관심 목록 조회
    @GetMapping("/me")
    @Operation(
            summary = "사용자 관심 목록 조회",
            description = "현재 로그인된 사용자의 관심 알림 설정 목록을 조회합니다.",
            security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<List<UserInterestResponseDto>> getUserInterests(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId){
        List<UserInterestResponseDto> list = userInterestService.getUserInterests(userId);
        return ResponseEntity.ok(list);
    }

    // 관심 알림 삭제
    @DeleteMapping("/settings/{id}")
    @Operation(
            summary = "관심 알림 삭제",
            description = "사용자의 관심 알림 설정을 삭제합니다. ",
            security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<Void> deleteInterest(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Parameter(description = "삭제할 관심 설정 ID") @PathVariable Long id)
    {
        userInterestService.deleteInterest(userId, id);
        return ResponseEntity.noContent().build();
    }
}