package roarbits.ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roarbits.ai.service.AiRecommendationFacade;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiRecommandationController {
    private final AiRecommendationFacade facade;

    @GetMapping("/recommendations")
    public ResponseEntity<?> get(@RequestParam Long userId) {
        return ResponseEntity.ok(facade.recommendFromActiveTimetable(userId));
    }
}
