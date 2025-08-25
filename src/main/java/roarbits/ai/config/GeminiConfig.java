package roarbits.ai.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class GeminiConfig {
    @Value("${gemini.api-key}")
    private String geminiApiKey;

    @Bean
    public WebClient geminiWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
                .responseTimeout(Duration.ofSeconds(20))
                .doOnConnected(conn -> {
                conn.addHandlerLast(new ReadTimeoutHandler(30));
                conn.addHandlerLast(new WriteTimeoutHandler(30));
                });

        return WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public Runnable geminiConfigSelfCheck() {
        return () -> {
            if (geminiApiKey == null || geminiApiKey.isBlank()) {
                System.err.println("WARNING: Gemini API key is not set. AI features will not work.");
            }
        };
    }
}
