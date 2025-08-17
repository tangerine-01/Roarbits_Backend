package roarbits.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import okhttp3.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class GeminiTestController {
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    @Operation(summary = "GEMINI API 연결 확인")
    @GetMapping("/api/gemini/test")
    public String testGemini() {
        OkHttpClient client = new OkHttpClient();

        String json = "{ \"contents\": [{ \"parts\": [{ \"text\": \"안녕, 연결 테스트 중이야!\" }] }] }";
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(ENDPOINT + "?key=" + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body().string();
                JsonObject jsonObject = new JsonParser().parseString(responseBody).getAsJsonObject();
                JsonArray candidates = jsonObject.getAsJsonArray("candidates");
                String text = candidates
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString();
                return "GEMINI API 연결 성공: " + text;
            } else {
                return "❌ GEMINI API 연결 실패: " + response.message();
            }
            } catch (Exception e) {
            return "❌ GEMINI API 연결 중 오류 발생: " + e.getMessage();
        }
    }
}
