package roarbits.ai.controller;

import org.springframework.security.oauth2.jwt.Jwt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import roarbits.ai.service.AiRecommendationService;
import roarbits.timetable.dto.TimetableResponseDto;
import roarbits.timetable.dto.TimeSlotDto;
import roarbits.timetable.service.TimetableService;
import roarbits.user.entity.User;
import roarbits.user.repository.UserRepository;

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
    private final UserRepository userRepository;

    @PostMapping("/recommendation/auto")
    public ResponseEntity<List<String>> getRecommendation(Authentication auth) {
        Long userId = extractUserId(auth);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        TimetableResponseDto main = timetableService.getMainTimetableOptional(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "등록된 시간표가 없습니다."));

        String scheduleJson = toCompactScheduleJson(main);
        return ResponseEntity.ok(aiRecommendationService.generateRecommendation(scheduleJson));
    }

    private Long extractUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;

        if (auth instanceof JwtAuthenticationToken jat) {
            Jwt jwt = jat.getToken();
            Object v = jwt.getClaim("id");
            if (v instanceof Number n1) return n1.longValue();

            v = jwt.getClaim("userId");
            if (v instanceof Number n2) return n2.longValue();

            String sub = jwt.getSubject(); // 종종 username/email 또는 userId가 들어있음
            if (sub != null) {
                if (sub.matches("\\d+")) return Long.parseLong(sub);
                return findUserIdByUsernameOrEmail(sub);
            }
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            // UserDetails 구현체에 getId()가 있으면 사용
            try {
                var m = ud.getClass().getMethod("getId");
                Object v = m.invoke(ud);
                if (v instanceof Number n) return n.longValue();
            } catch (Exception ignored) {}
            // 없으면 username/email로 조회
            return findUserIdByUsernameOrEmail(ud.getUsername());
        }

        if (principal instanceof String s) {
            return findUserIdByUsernameOrEmail(s);
        }

        return null;
    }

    private Long findUserIdByUsernameOrEmail(String key) {
        if (key == null || key.isBlank()) return null;

        Optional<User> byEmail = userRepository.findByEmail(key);
        if (byEmail.isPresent()) return byEmail.get().getId();

        try {
            var method = userRepository.getClass().getMethod("findByUsername", String.class);
            @SuppressWarnings("unchecked")
            Optional<User> byUsername = (Optional<User>) method.invoke(userRepository, key);
            if (byUsername != null && byUsername.isPresent()) return byUsername.get().getId();
        } catch (NoSuchMethodException nsme) {
            // 레포에 findByUsername 없으면 무시
        } catch (Exception ignored) {}

        return null;
    }

    private String toCompactScheduleJson(TimetableResponseDto tt) {
        if (tt == null || tt.getTimeSlots() == null || tt.getTimeSlots().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "등록된 시간표가 없습니다.");
        }

        ZoneId ZONE = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(ZONE);
        LocalTime nowTime = now.toLocalTime();
        int todayIdx = dayIndexDomainAligned(now.getDayOfWeek()); // 0=월 ~ 6=일

        List<Map<String, Object>> slots = new ArrayList<>();
        for (TimeSlotDto s : tt.getTimeSlots()) {
            if (s == null) continue;

            Integer day = normalizeDay(s.getDay()); // int/String/enum 모두 수용
            if (day == null || day < 0 || day > 6) continue;

            LocalTime st = parseLocalTimeSafe(s.getStartTime());
            LocalTime et = parseLocalTimeSafe(s.getEndTime());
            if (st == null || et == null || !st.isBefore(et)) continue;

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("subject", nvl(s.getSubjectName(), "수업"));
            m.put("day", day);                 // 0(월)~6(일)
            m.put("start", st.toString());     // HH:mm
            m.put("end", et.toString());       // HH:mm
            putIfNotBlank(m, "code", s.getSubjectName());
            putIfNotBlank(m, "room", s.getClassroom());
            putIfNotBlank(m, "professor", s.getProfessor());
            slots.add(m);
        }

        if (slots.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효한 시간표 항목이 없습니다.");
        }

        slots.sort(Comparator
                .comparing((Map<String, Object> m) -> (Integer) m.get("day"))
                .thenComparing(m -> LocalTime.parse((String) m.get("start"))));

        Map<String, Object> currentClass = null;
        for (Map<String, Object> m : slots) {
            int d = (Integer) m.get("day");
            if (d != todayIdx) continue;
            LocalTime st = LocalTime.parse((String) m.get("start"));
            LocalTime et = LocalTime.parse((String) m.get("end"));
            if (!nowTime.isBefore(st) && nowTime.isBefore(et)) {
                currentClass = m;
                break;
            }
        }

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("timezone", ZONE.getId());
        root.put("today", dayShortKOR(now.getDayOfWeek())); // "월","화",...
        root.put("todayIndex", todayIdx);                  // 0=월 ~ 6=일
        root.put("now", nowTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        root.put("hasOngoingClass", currentClass != null);

        if (currentClass != null) {
            Map<String, Object> cur = new LinkedHashMap<>();
            cur.put("subject", currentClass.get("subject"));
            cur.put("start", currentClass.get("start"));
            cur.put("end", currentClass.get("end"));
            cur.put("room", currentClass.getOrDefault("room", ""));
            cur.put("building", currentClass.getOrDefault("building", ""));
            root.put("currentClass", cur);
        }

        Map<Integer, List<Map<String, Object>>> byDay = new LinkedHashMap<>();
        for (int i = 0; i <= 6; i++) byDay.put(i, new ArrayList<>());
        for (Map<String, Object> m : slots) byDay.get((Integer) m.get("day")).add(m);

        List<Map<String, Object>> days = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            List<Map<String, Object>> items = byDay.get(i);
            if (items.isEmpty()) continue;
            List<Map<String, Object>> lectures = new ArrayList<>();
            for (Map<String, Object> m : items) {
                Map<String, Object> mm = new LinkedHashMap<>();
                mm.put("subject", m.get("subject"));
                mm.put("start", m.get("start"));
                mm.put("end", m.get("end"));
                if (m.containsKey("room")) mm.put("room", m.get("room"));
                if (m.containsKey("building")) mm.put("building", m.get("building"));
                lectures.add(mm);
            }
            Map<String, Object> dayBlock = new LinkedHashMap<>();
            dayBlock.put("dayIndex", i);      // 0=월, ... 6=일
            dayBlock.put("lectures", lectures);
            days.add(dayBlock);
        }
        root.put("days", days);

        try {
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "시간표 변환 실패");
        }
    }


    private int dayIndexDomainAligned(DayOfWeek dow) {
        // MON(1)→0, TUE(2)→1, ..., SUN(7)→6
        return (dow.getValue() + 6) % 7;
    }

    private Integer normalizeDay(Object raw) {
        if (raw == null) return null;

        if (raw instanceof Integer i) {
            // 케이스 A) 이미 0=월~6=일 → 그대로 허용
            if (i >= 0 && i <= 6) return i;

            // 케이스 B) 0=일~6=토 체계 → 0(일)→6, 1(월)→0, 2→1 ...
            if (i == 0) return 6;                // 일→6
            if (i >= 1 && i <= 6) return i - 1;  // 월~토 → 0~5
            return null;
        }

        if (raw instanceof String s) {
            String up = s.trim().toUpperCase(Locale.ROOT);
            switch (up) {
                case "MON", "MONDAY", "월", "월요일": return 0;
                case "TUE", "TUESDAY", "화", "화요일": return 1;
                case "WED", "WEDNESDAY", "수", "수요일": return 2;
                case "THU", "THURSDAY", "목", "목요일": return 3;
                case "FRI", "FRIDAY", "금", "금요일": return 4;
                case "SAT", "SATURDAY", "토", "토요일": return 5;
                case "SUN", "SUNDAY", "일", "일요일": return 6;
                default: return null;
            }
        }

        if (raw instanceof DayOfWeek dow) {
            return dayIndexDomainAligned(dow);
        }

        return null;
    }

    // "HH:mm", "HH:mm:ss", "H:m" 모두 수용 → HH:mm로 통일
    private LocalTime parseLocalTimeSafe(String v) {
        if (v == null || v.isBlank()) return null;
        String s = v.trim();
        try {
            LocalTime t = LocalTime.parse(s); // 표준 형식 우선
            return LocalTime.of(t.getHour(), t.getMinute());
        } catch (Exception ignore) {
            // "H:m" 같은 자유 형식
            try {
                String[] p = s.split(":");
                int h = Integer.parseInt(p[0]);
                int m = (p.length > 1) ? Integer.parseInt(p[1]) : 0;
                return LocalTime.of(h, m);
            } catch (Exception e) {
                return null;
            }
        }
    }

    private String dayShortKOR(DayOfWeek dow) {
        switch (dow) {
            case MONDAY: return "월";
            case TUESDAY: return "화";
            case WEDNESDAY: return "수";
            case THURSDAY: return "목";
            case FRIDAY: return "금";
            case SATURDAY: return "토";
            case SUNDAY: return "일";
        }
        return "";
    }

    private static void putIfNotBlank(Map<String, Object> map, String key, String val) {
        if (val != null && !val.isBlank()) map.put(key, val);
    }

    private static String nvl(String s, String d) {
        return (s == null || s.isBlank()) ? d : s;
    }
}
