package roarbits.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeminiConfig {
    @Bean
    public WebClient geminiWebClient() {
        return WebClient.builder()
                .baseUrl("https://gemini.googleapis.com")
                .build();
    }
}
