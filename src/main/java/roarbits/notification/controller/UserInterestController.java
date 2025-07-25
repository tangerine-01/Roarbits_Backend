package roarbits.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<UserInterestResponseDto> saveOrUpdateInterest(
            @RequestBody UserInterestRequestDto requestDto) {
        UserInterestResponseDto responseDto = userInterestService.saveOrUpdateInterest(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 사용자 관심 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserInterestResponseDto>> getUserInterests(
            @PathVariable Long userId) {
        List<UserInterestResponseDto> interests = userInterestService.getUserInterests(userId);
        return ResponseEntity.ok(interests);
    }

    // 관심 알림 삭제
    @DeleteMapping("/settings/{id}")
    public ResponseEntity<Void> deleteInterest(@PathVariable Long id) {
        userInterestService.deleteInterest(id);
        return ResponseEntity.noContent().build();
    }
}