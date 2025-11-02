package com.YouTube.Tool.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    // ✅ Updated model endpoint for Gemini 2.5
    private static final String MODEL_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    @Value("${gemini.timeout-ms:60000}") // ✅ 60 seconds default
    private long timeoutMs;

    public AiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String chatWithGemini(String userPrompt) {
        try {
            log.info("💬 Sending prompt to Gemini: {}", userPrompt);
            Instant startTime = Instant.now();

            // ✅ Request body format for Gemini 2.5 API
            Map<String, Object> requestBody = Map.of(
                    "contents", new Object[]{
                            Map.of("parts", new Object[]{Map.of("text", userPrompt)})
                    }
            );

            // ✅ Send POST request
            Map<String, Object> response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-2.5-flash:generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofMillis(timeoutMs))
                    .retry(1) // 🔁 Retry once if timeout or temporary network issue
                    .onErrorResume(err -> {
                        log.error("⚠️ Gemini API Error: {}", err.getMessage());
                        return Mono.just(Map.of("error", err.getMessage()));
                    })
                    .block();

            long responseTime = Duration.between(startTime, Instant.now()).toMillis();
            log.info("⏱️ Gemini response time: {} ms", responseTime);

            if (response == null || !response.containsKey("candidates")) {
                return "⚠️ No response received from Gemini API.";
            }

            var candidates = (Iterable<Map<String, Object>>) response.get("candidates");
            for (Map<String, Object> candidate : candidates) {
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                if (content != null && content.containsKey("parts")) {
                    var parts = (Iterable<Map<String, Object>>) content.get("parts");
                    for (Map<String, Object> part : parts) {
                        return (String) part.get("text");
                    }
                }
            }

            return "⚠️ Unable to parse Gemini API response.";

        } catch (Exception e) {
            log.error("❌ Exception in AiService: {}", e.getMessage(), e);
            return "Error: Failed to connect with Gemini API.";
        }
    }
}
