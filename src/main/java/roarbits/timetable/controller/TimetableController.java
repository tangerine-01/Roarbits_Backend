package roarbits.timetable.controller;

import roarbits.timetable.dto.TimetableRequestDto;
import roarbits.timetable.dto.TimetableResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import roarbits.timetable.service.TimetableService;

@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService timetableService;

    // 시간표 전체 조회 (userId 기준)
    @GetMapping("/{userId}")
    public ResponseEntity<List<TimetableResponseDto>> getAllTimetables(@PathVariable Long userId) {
        List<TimetableResponseDto> timetables = timetableService.getTimetablesByUser(userId);
        return ResponseEntity.ok(timetables);
    }

    // 시간표 생성
    @PostMapping
    public ResponseEntity<TimetableResponseDto> createTimetable(@RequestBody TimetableRequestDto requestDto) {
        TimetableResponseDto created = timetableService.createTimetable(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 시간표 수정
    @PutMapping("/{timetableId}")
    public ResponseEntity<TimetableResponseDto> updateTimetable(
            @PathVariable Long timetableId,
            @RequestBody TimetableRequestDto requestDto)
    {
        TimetableResponseDto updated = timetableService.updateTimetable(timetableId, requestDto);
        return ResponseEntity.ok(updated);
    }

    // 시간표 삭제
    @DeleteMapping("/{timetableId}")
    public ResponseEntity<Void> deleteTimetable(@PathVariable Long timetableId) {
        timetableService.deleteTimetable(timetableId);
        return ResponseEntity.noContent().build();
    }
}
