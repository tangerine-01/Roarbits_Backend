package roarbits.timetable.controller;

import jakarta.validation.Valid;
import roarbits.timetable.dto.TimetableRequestDto;
import roarbits.timetable.dto.TimetableResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import java.util.List;
import roarbits.timetable.service.TimetableService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Timetable", description = "시간표 관리 API")
@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")

public class TimetableController {
    private final TimetableService timetableService;

    // 시간표 전체 조회 (/me 기준)
    @GetMapping("/me")
    @Operation(
            summary = "시간표 전체 조회",
            description = "현재 로그인된 사용자의 모든 시간표를 조회합니다.",
            security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<List<TimetableResponseDto>> getAllTimetables(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId
    ){
        return ResponseEntity.ok(timetableService.getTimetablesByUser(userId));
    }

    // 시간표 생성
    @PostMapping
    @Operation(
            summary = "시간표 생성",
            description = "현재 로그인된 사용자의 시간표를 생성합니다.",
            security = {@SecurityRequirement(name = "Authorization")})
    public ResponseEntity<TimetableResponseDto> createTimetable(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "생성할 시간표 정보")
            @Valid @org.springframework.web.bind.annotation.RequestBody TimetableRequestDto requestDto
    ){
        var created = timetableService.createTimetable(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 시간표 수정
    @PutMapping("/{timetableId}")
    @Operation(
            summary = "시간표 수정",
            description = "현재 로그인된 사용자의 시간표를 수정합니다.",
            security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<TimetableResponseDto> updateTimetable(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Parameter(description = "수정할 시간표 ID") @PathVariable Long timetableId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "수정할 시간표 정보")
            @Valid @org.springframework.web.bind.annotation.RequestBody TimetableRequestDto requestDto
    ) {
        var updated = timetableService.updateTimetable(userId, timetableId, requestDto);
        return ResponseEntity.ok(updated);
    }

    // 시간표 삭제
    @DeleteMapping("/{timetableId}")
    @Operation(
            summary = "시간표 삭제",
            description = "현재 로그인된 사용자의 시간표를 삭제합니다.",
            security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<Void> deleteTimetable(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Parameter(description = "삭제할 시간표 ID") @PathVariable Long timetableId
    ) {
        timetableService.deleteTimetable(userId, timetableId);
        return ResponseEntity.noContent().build();
    }
}
