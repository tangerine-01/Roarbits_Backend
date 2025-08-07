package roarbits.ai.controller;

import roarbits.ai.service.AiRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiRecommendationService aiRecommendationService;

    @PostMapping("/recommendation")
    public ResponseEntity<List<String>> getRecommendation(@RequestBody String scheduleJson) {
        List<String> result = aiRecommendationService.generateRecommendation(scheduleJson);
        return ResponseEntity.ok(result);
    }
}
