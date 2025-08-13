package roarbits.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;

import roarbits.notification.dto.UserInterestRequestDto;
import roarbits.notification.dto.UserInterestResponseDto;
import roarbits.notification.service.UserInterestService;

@RestController
@RequestMapping("/api/interest")
@RequiredArgsConstructor
public class UserInterestController {

    private final UserInterestService userInterestService;

    // 관심 알림 설정 등록 또는 수정
    @PostMapping("/settings")
    @Operation(security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<UserInterestResponseDto> saveOrUpdateInterest(
            @RequestBody UserInterestRequestDto requestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        UserInterestResponseDto responseDto = userInterestService.saveOrUpdateInterest(userId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 사용자 관심 목록 조회
    @GetMapping("/me")
    @Operation(security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<List<UserInterestResponseDto>> getUserInterests(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId){
        List<UserInterestResponseDto> interests = userInterestService.getUserInterests(userId);
        return ResponseEntity.ok(interests);
    }

    // 관심 알림 삭제
    @DeleteMapping("/settings/{id}")
    @Operation(security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<Void> deleteInterest(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId) {
        userInterestService.deleteInterest(userId, id);
        return ResponseEntity.noContent().build();
    }
}