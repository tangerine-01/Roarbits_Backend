package roarbits.ai.controller;

import org.springframework.security.oauth2.jwt.Jwt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import roarbits.ai.service.AiRecommendationService;
import roarbits.timetable.dto.TimetableResponseDto;
import roarbits.timetable.dto.TimeSlotDto;
import roarbits.timetable.service.TimetableService;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiRecommendationService aiRecommendationService;
    private final TimetableService timetableService;
    private final ObjectMapper objectMapper;

    @PostMapping("/recommendation/auto")
    public ResponseEntity<List<String>> getRecommendation(
            @AuthenticationPrincipal Jwt jwt){
        Long userId = extractUserId(jwt);
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");

        TimetableResponseDto main = timetableService.getMainTimetableOptional(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "등록된 시간표가 없습니다."));

        String scheduleJson = toCompactScheduleJson(main);
        return ResponseEntity.ok(aiRecommendationService.generateRecommendation(scheduleJson));
    }

    private Long extractUserId(Jwt jwt) {
        if (jwt == null) return null;
        Object v = jwt.getClaim("id");
        if (v instanceof Number n1) return n1.longValue();
        v = jwt.getClaim("userId");
        if (v instanceof Number n2) return n2.longValue();
        String sub = jwt.getSubject();
        return (sub != null && sub.matches("\\d+")) ? Long.parseLong(sub) : null;
    }

    private String toCompactScheduleJson(TimetableResponseDto tt) {
        ZoneId KST = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(KST);
        String nowStr = now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        int todayIx = dayOfWeekToIndex(now.getDayOfWeek());
        boolean classOngoing = isClassOngoingNow(tt.getTimeSlots(), todayIx, now.toLocalTime());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("today", dayOfWeekShort(now.getDayOfWeek()));
        map.put("now", nowStr);
        map.put("classOngoing", classOngoing ? "on_campus" : "off_campus");

        List<Map<String, Object>> slots = new ArrayList<>();
        tt.getTimeSlots().forEach(s -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("subject", s.getSubjectName());
            m.put("startTime", s.getStartTime());
            m.put("endTime", s.getEndTime());
            m.put("day", s.getDay());
            slots.add(m);
        });
        map.put("slots", slots);

        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "시간표 변환 실패");
        }
    }

    private boolean isClassOngoingNow(List<TimeSlotDto> slots, int todayIdx, LocalTime now) {
        for(TimeSlotDto s : slots) {
            if (s.getDay() == null) continue;
            if (!Objects.equals(s.getDay(), todayIdx)) continue;

            try {
                LocalTime start = LocalTime.parse(s.getStartTime());
                LocalTime end = LocalTime.parse(s.getEndTime());
                if (!now.isBefore(start) && !now.isAfter(end)) {
                    return true;
                }
            } catch (Exception ignored) {
                // 무시하고 계속 진행
            }
        }
        return false;
    }

    private int dayOfWeekToIndex(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY -> 6;
            case SUNDAY -> 0;
        };
    }

    private String dayOfWeekShort(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Mon";
            case TUESDAY -> "Tue";
            case WEDNESDAY -> "Wed";
            case THURSDAY -> "Thu";
            case FRIDAY -> "Fri";
            case SATURDAY -> "Sat";
            case SUNDAY -> "Sun";
        };
    }
}
