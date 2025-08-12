package roarbits.ai.service;

import org.springframework.stereotype.Service;
import roarbits.ai.prompt.SchedulePromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {
    private final WebClient geminiWebClient;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    public List<String> generateRecommendation(String scheduleJson) {
        String prompt = SchedulePromptBuilder.build(scheduleJson) +
                "\n3개의 행동 추천을 bullet 형식으로 제공해줘.";

        Map<String,Object> requestBody = Map.of(
                "contexts", List.of(
                        Map.of(
                                "parts", List.of(Map.of("text", prompt))
                ))
        );

        String response = geminiWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-1.5-flash:generateContent")
                        .queryParam("key", geminiApiKey)
                        .build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(error -> Mono.just("{\"candidates\": [{\"content\": {\"parts\": [{\"text\": \"추천 문구 생성에 실패했습니다.\"}]}}]"))
                .block();

        return extractTextList(response);
    }

    private List<String> extractTextList(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            String text = root.path("candidates")
                        .get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .path("text")
                        .asText();

            List<String> result = new ArrayList<>();
            for (String line : text.split("\n")) {
                String trimmed = line.replaceFirst("^- ", "").trim();
                if(trimmed.isEmpty()) result.add(trimmed);
            }
            return result;
        } catch (Exception e) {
            return List.of ("추천문구를 파싱하는 데 실패했습니다.");
        }
    }
}
