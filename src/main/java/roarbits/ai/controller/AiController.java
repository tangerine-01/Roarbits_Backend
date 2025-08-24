package roarbits.ai.controller;

import roarbits.ai.service.AiRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiRecommendationService aiRecommendationService;

    @PostMapping("/recommendation")
    public ResponseEntity<List<String>> getRecommendation(@RequestBody Map<String, Object> body) {
        String scheduleJson = String.valueOf(body.getOrDefault("scheduleJson", "{}"));
        return ResponseEntity.ok(aiRecommendationService.generateRecommendation(scheduleJson));
    }
}
