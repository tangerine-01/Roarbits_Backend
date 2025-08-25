package roarbits.ai.service;

import org.springframework.stereotype.Service;
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
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roarbits.ai.dto.RecommendationResponseDto;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {
    private final WebClient geminiWebClient;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Pattern BULLET_PREFIX = Pattern.compile("^\\s*([-*•\\d\\.\\)]+)\\s*");

    private String fallbackJson(String msg) {
        return """
    {"candidates":[{"content":{"parts":[{"text":"%s"}]}}]}
    """.formatted(msg);
    }

    private static final Logger log = LoggerFactory.getLogger(AiRecommendationService.class);

    public List<String> generateRecommendation(String scheduleJson) {
        // 프롬프트 문자열에 개행이 있어도 bodyValue로 넘기면 Jackson이 자동 이스케이프함.
        String prompt =
                "다음은 사용자의 주간 시간표 JSON입니다.\n" +
                        scheduleJson + "\n\n" +
                        "각 항목은 한 줄 60자 이내로 간결하게." +
                 "학교는 한국항공대학교 기준으로 공강 시간 또는 빈 시간에 주변 지역 상권에서 학생들이 할 수 있는 활동을 추천해줘." +
                 "(택시팟, 밥팟, 스터디팟 등)을 통해 학교 앞 골목상권 및 로컬 비즈니스 소비 유도하는 방향으로 사용자가 실제로 할 수 있는 활동 기반으로" +
                 "실제 장소를 언급해줘";

        // ★ 핵심: 'contents'가 정식 필드명 (기존 코드 'contexts'는 오타)
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        // 요청 보내기 (절대 문자열로 JSON을 직접 만들지 말고 bodyValue 사용)
        String raw = geminiWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-1.5-flash:generateContent")
                        .queryParam("key", geminiApiKey)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.isError(), this::toApiError)
                .bodyToMono(String.class)   // 우선 문자열로 받고,
                .block();

        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("[AI] 빈 응답");
        }

        String sanitized = stripXssiPrefix(raw);

        JsonNode root;
        try {
            root = objectMapper.readTree(sanitized);
        } catch (Exception e) {
            log.error("[AI] 응답 파싱 실패. 원본 응답(앞 500자): {}", sanitized.substring(0, Math.min(500, sanitized.length())));
            throw new IllegalStateException("응답 파싱 실패", e);
        }

        // candidates[0].content.parts[*].text 추출
        JsonNode candidates = root.path("candidates");
        if (!candidates.isArray() || candidates.isEmpty()) {
            throw new IllegalStateException("[AI] candidates가 비었습니다: " + sanitized);
        }
        JsonNode parts = candidates.get(0).path("content").path("parts");
        if (!parts.isArray() || parts.isEmpty()) {
            throw new IllegalStateException("[AI] parts가 비었습니다: " + sanitized);
        }
        String text = parts.get(0).path("text").asText("");

        // 애초에 한 번에 bullet 3개를 text로 주게 했으므로, 개행으로 분리
        return text.lines()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .limit(3)
                .toList();
    }

    private static String stripXssiPrefix(String s) {
        // 예: ")]}'\n{...}" 형태라면 프리픽스 제거
        if (s.startsWith(")]}'")) {
            int i = s.indexOf('\n');
            return (i >= 0) ? s.substring(i + 1) : s;
        }
        return s;
    }

    public List<RecommendationResponseDto.Item> generateRecommendationItems(String scheduleJson, String purpose) {
        List<String> lines = generateRecommendation(scheduleJson);

        List<RecommendationResponseDto.Item> items = new ArrayList<>();
        for (String s : lines) {
            if (s == null || s.isBlank()) continue;
            items.add(RecommendationResponseDto.Item.builder()
                    .id(UUID.randomUUID().toString())
                    .text(s.trim())
                    .build());
        }
        return items;
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
            ArrayNode candidates = root.has("candidates") && root.get("candidates").isArray()
                    ? (ArrayNode) root.get("candidates") : null;
            if (candidates == null || candidates.isEmpty()) {
                return List.of("추천 문구가 없습니다.");
            }

            JsonNode first = candidates.get(0);
            if (first == null) {
                return List.of("추천 문구가 없습니다.");
            }

            JsonNode content = candidates.get(0).path("content");

            ArrayNode parts = content.has("parts") && content.get("parts").isArray()
                    ? (ArrayNode) content.get("parts") : null;
            if (parts == null || parts.isEmpty()) {
                return List.of("추천 문구가 없습니다.");
            }

            String joined = toStream(parts)
                    .map(p -> p.path("text").asText(""))
                    .filter(s -> s !=null && !s.isBlank())
                    .collect(Collectors.joining("\n"));

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
