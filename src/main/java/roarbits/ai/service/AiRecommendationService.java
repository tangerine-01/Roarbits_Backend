package roarbits.ai.service;

import org.springframework.stereotype.Service;
import roarbits.ai.prompt.SchedulePromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.time.Duration;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {
    private final WebClient geminiWebClient;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Pattern BULLET_PREFIX = Pattern.compile("^\\s*([-*•\\d\\.\\)]+)\\s*");

    public List<String> generateRecommendation(String scheduleJson) {
        String prompt = SchedulePromptBuilder.build(scheduleJson)
                + "\n다음 조건을 지켜서 3개의 행동 추천을 bullet 형식으로 제공해줘."
                + "\n- 한 줄에 하나씩, 20~60자. \n- 실행 가능한 구체 동사로 시작.";

        // 요청 본문
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ));

        // Gemini API 호출
        String response = geminiWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-1.5-flash:generateContent")
                        .queryParam("key", geminiApiKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
                        this::toApiError)
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(20))
                .onErrorResume(error -> Mono.just("{\"candidates\": [{\"content\": {\"parts\": [{\"text\": \"추천 문구 생성에 실패했습니다.\"}]}}]}"))
                .block();

        return extractTextList(response);
    }

    private Mono<? extends Throwable> toApiError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("API 요청 실패: 응답 본문이 비어 있습니다.")
                .flatMap(body -> {
                    HttpStatusCode st = response.statusCode();
                    if (st.is5xxServerError()) {
                        return  Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI 서비스가 일시적으로 중단되었습니다. 잠시 후 다시 시도해주세요."));
                    }
                    if (st.value() == 429) {
                        return Mono.error(new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "AI 서비스 요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요."));
                    }
                    return Mono.error(new ResponseStatusException(st, "API 요청 실패: " + body));
                });
    }

    private List<String> extractTextList(String response) {
        try {
            JsonNode root = mapper.readTree(response);
            ArrayNode candidates = (ArrayNode) root.path("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return List.of("추천 문구가 없습니다.");
            }

            JsonNode content = candidates.get(0).path("content");
            ArrayNode parts = (ArrayNode) content.path("parts");

            if(parts == null || parts.isEmpty()) {
                return List.of("추천 문구가 없습니다.");
            }

            String joined = new StringBuilder()
                    .append(
                            toStream(parts)
                                    .map(p -> p.path("text").asText(""))
                                    .filter(s -> s !=null && !s.isBlank())
                                    .collect(Collectors.joining("\n"))
                    ).toString();

            if (joined.isBlank()) {
                return List.of("추천문구가 생성되지 않았습니다.");
            }

            List<String> lines = joined.lines()
                    .map(s -> BULLET_PREFIX.matcher(s).replaceFirst(""))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toList());

            if (lines.isEmpty()) {
                return List.of("추천 문구가 생성되지 않았습니다.");
            }

            List<String> out = new ArrayList<>();
            for (String s : lines) {
                String trimmed = s.length() > 70 ? s.substring(0, 67) + "..." : s;
                out.add(trimmed);
                if (out.size() == 3) break;
            }
            return out;

        } catch (Exception e) {
            return List.of("응답 파싱 실패: " + e.getMessage());
        }
    }

    private static java.util.stream.Stream<JsonNode> toStream(ArrayNode array) {
        List<JsonNode> list = new ArrayList<>();
        array.forEach(list::add);
        return list.stream();
    }
}
