package com.fitnes.aiService.serviceImpl;

import com.fitnes.aiService.service.GeminiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class GeminiServiceImpl implements GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String GEMINI_API_URL;

    @Value("${gemini.api.key}")
    private String GEMINI_API_KEY;

    public GeminiServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String getAnswer(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of(
                                                "text", prompt
                                        )
                                }
                        )
                });
        log.info("Sending request to Gemini API with prompt: {}", requestBody.toString());

        String response = webClient.post()
                .uri(GEMINI_API_URL)
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", GEMINI_API_KEY)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return response;
    }
}
