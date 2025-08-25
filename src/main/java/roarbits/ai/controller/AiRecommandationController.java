package roarbits.ai.controller;

import jakarta.validation.Valid;
import roarbits.ai.dto.*;
import roarbits.ai.facade.AiPublishFacade;
import roarbits.ai.service.AiRecommendationService;
import roarbits.community.dto.CommunityResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiRecommandationController {
    private final AiRecommendationService aiService;
    private final AiPublishFacade publishFacade;

    @PostMapping
    public ResponseEntity<RecommendationResponseDto> getRecommendations(
            @Valid @RequestBody RecommendationRequestDto req) {
        List<RecommendationResponseDto.Item> items =
                aiService.generateRecommendationItems(req.getScheduleJson(), req.getPurpose());
        return ResponseEntity.ok(RecommendationResponseDto.builder().items(items).build());
    }

    @PostMapping("/publish")
    public ResponseEntity<CommunityResponseDto.Post> publishRecommendation(
            @Valid @RequestBody PublishRecommendationRequestDto req) {
        // writerId 존재 검증(없으면 404/400 던지기)
        CommunityResponseDto.Post post = publishFacade.publish(
                req.getWriterId(), req.getTitle(), req.getContent(), req.getPostType());
        return ResponseEntity.ok(post);
    }
}
