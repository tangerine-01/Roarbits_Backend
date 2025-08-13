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

@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService timetableService;

    // 시간표 전체 조회 (/me 기준)
    @GetMapping("/me")
    @Operation(security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<List<TimetableResponseDto>> getAllTimetables(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId
    ){
        return ResponseEntity.ok(timetableService.getTimetablesByUser(userId));
    }

    // 시간표 생성
    @PostMapping
    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    public ResponseEntity<TimetableResponseDto> createTimetable(
            @Valid @RequestBody TimetableRequestDto requestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId
    ){
        var created = timetableService.createTimetable(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 시간표 수정
    @PutMapping("/{timetableId}")
    @Operation(security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<TimetableResponseDto> updateTimetable(
            @PathVariable Long timetableId,
            @RequestBody TimetableRequestDto requestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        var updated = timetableService.updateTimetable(userId, timetableId, requestDto);
        return ResponseEntity.ok(updated);
    }

    // 시간표 삭제
    @DeleteMapping("/{timetableId}")
    @Operation(security = { @SecurityRequirement(name = "Authorization")})
    public ResponseEntity<Void> deleteTimetable(
            @PathVariable Long timetableId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        timetableService.deleteTimetable(userId, timetableId);
        return ResponseEntity.noContent().build();
    }
}
