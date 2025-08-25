package roarbits.ai.service;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import roarbits.timetable.entity.TimeSlot;
import roarbits.timetable.entity.Timetable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchedulePayload {
    private Long timetableId;
    private String timezone; // e.g., "Asia/Seoul"
    private int weekStart;   // 1 = Monday
    private List<Slot> slots;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Slot {
        private int day;       // 1~7 (Mon~Sun)
        private int startMin;  // minutes from 00:00
        private int endMin;    // minutes from 00:00
        private String subject;
        private String place;
    }

    public static SchedulePayload from(Timetable t, ZoneId zoneId) {
        List<Slot> list = new ArrayList<>();
        if (t.getTimeSlots() != null) {
            for (TimeSlot s : t.getTimeSlots()) {
                Object subj = safeInvoke(s, "getSubject"); // Subject 또는 null
                int day = toMon1Sun7FromSubject(subj);

                String subjectName = "";
                if (subj != null) {
                    Object name = safeInvoke(subj, "getName");
                    subjectName = optional(name == null ? null : name.toString());
                }

                LocalTime st = (LocalTime) safeInvoke(s, "getStartTime");
                LocalTime et = (LocalTime) safeInvoke(s, "getEndTime");

                list.add(Slot.builder()
                        .day(day)
                        .startMin(toMin(st))
                        .endMin(toMin(et))
                        .subject(subjectName)
                        .place(optional(getPlaceSafe(s)))
                        .build());
            }
        }

        return SchedulePayload.builder()
                .timetableId(getTimetableIdSafe(t))
                .timezone(zoneId != null ? zoneId.getId() : ZoneId.systemDefault().getId())
                .weekStart(1)
                .slots(list)
                .build();
    }

    /* ----------------- Helpers ----------------- */

    private static int toMin(LocalTime t) {
        if (t == null) return 0;
        return t.getHour() * 60 + t.getMinute();
    }

    private static String optional(String s) {
        return s == null ? "" : s;
    }

    private static Long getTimetableIdSafe(Timetable t) {
        if (t == null) return null;
        // 1) getId()
        Object v1 = safeInvoke(t, "getId");
        if (v1 instanceof Long) return (Long) v1;
        // 2) getTimetableId()
        Object v2 = safeInvoke(t, "getTimetableId");
        if (v2 instanceof Long) return (Long) v2;
        // 3) 다른 PK 타입이면 문자열로 찍어 디버그만
        if (v1 != null || v2 != null) {
            log.debug("Timetable PK not Long: id={}, timetableId={}", v1, v2);
        }
        return null;
    }

    private static int toMon1Sun7FromSubject(Object subject) {
        if (subject == null) return 1; // 기본 월요일
        Object dow = safeInvoke(subject, "getDayOfWeek");
        if (dow == null) return 1;

        if (dow instanceof Integer) {
            int v = (Integer) dow;
            if (v >= 1 && v <= 7) return v;
            if (v >= 0 && v <= 6) return v == 0 ? 1 : v + 1;
        }

        if (dow instanceof DayOfWeek d) {
            return d.getValue(); // MONDAY=1 ... SUNDAY=7
        }

        if (dow instanceof String s) {
            String u = s.trim().toUpperCase();
            switch (u) {
                case "MONDAY", "MON", "1": return 1;
                case "TUESDAY", "TUE", "2": return 2;
                case "WEDNESDAY", "WED", "3": return 3;
                case "THURSDAY", "THU", "4": return 4;
                case "FRIDAY", "FRI", "5": return 5;
                case "SATURDAY", "SAT", "6": return 6;
                case "SUNDAY", "SUN", "7": return 7;
            }
            try {
                int v = Integer.parseInt(u);
                if (v >= 1 && v <= 7) return v;
                if (v >= 0 && v <= 6) return v == 0 ? 1 : v + 1;
            } catch (NumberFormatException ignore) {}
        }

        return 1;
    }

    private static String getPlaceSafe(Object timeSlot) {
        if (timeSlot == null) return "";
        Object v;

        v = safeInvoke(timeSlot, "getPlace");
        if (v != null) return v.toString();

        v = safeInvoke(timeSlot, "getLocation");
        if (v != null) return v.toString();

        v = safeInvoke(timeSlot, "getRoom");
        if (v != null) return v.toString();

        return "";
    }

    private static Object safeInvoke(Object target, String method) {
        if (target == null) return null;
        try {
            var m = target.getClass().getMethod(method);
            return m.invoke(target);
        } catch (Exception ignore) {
            return null;
        }
    }

    public String hash() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            sb.append(timetableId).append('|').append(timezone).append('|').append(weekStart);
            if (slots != null) {
                for (Slot s : slots) {
                    sb.append('|')
                            .append(s.getDay()).append(',')
                            .append(s.getStartMin()).append(',')
                            .append(s.getEndMin()).append(',')
                            .append(s.getSubject()).append(',')
                            .append(s.getPlace());
                }
            }
            byte[] b = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < Math.min(8, b.length); i++) hex.append(String.format("%02x", b[i]));
            return hex.toString();
        } catch (Exception e) {
            return "na";
        }
    }
}
