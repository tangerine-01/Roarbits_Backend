package roarbits.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roarbits.timetable.entity.Timetable;
import roarbits.timetable.repository.TimetableRepository;

import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiRecommendationFacade {
    private final TimetableRepository timetableRepository;
    private final AiRecommendationService aiRecommendationService;
    private final ObjectMapper objectMapper;

    public List<String> recommendFromActiveTimetable(Long userId) {
        Timetable active = timetableRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new IllegalStateException("활성 시간표가 없습니다. 먼저 활성화하세요."));

        SchedulePayload payload = SchedulePayload.from(active, ZoneId.of("Asia/Seoul"));
        String scheduleJson;
        try {
            scheduleJson = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("시간표 직렬화 실패", e);
        }

        log.info("AI-call user={}, activeTimetable={}, hash={}",
                userId, active.getId(), payload.hash());

        return aiRecommendationService.generateRecommendation(scheduleJson);
    }
}
