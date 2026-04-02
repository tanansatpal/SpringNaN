package com.example.demo.services;

import com.example.demo.dto.ClaudeMessage;
import com.example.demo.dto.ClaudeRequest;
import com.example.demo.dto.ClaudeResponse;
import com.example.demo.dto.CommentModerationResult;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ClaudeService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String sentimentModel;
    private final String replyModel;

    public ClaudeService(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${app.claude.api-key}") String apiKey,
            @Value("${app.claude.base-url}") String baseUrl,
            @Value("${app.claude.sentiment-model}") String sentimentModel,
            @Value("${app.claude.reply-model}") String replyModel
    ) {
        this.objectMapper = objectMapper;
        this.sentimentModel = sentimentModel;
        this.replyModel = replyModel;

        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public CommentModerationResult moderateAndReply(String commentText) {
        SentimentPayload payload = isNegative(commentText);
        if (payload.negative()) {
            return new CommentModerationResult(
                    true,
                    payload.reason(),
                    "Thanks for your feedback. Your comment has been flagged for review so the team can take a closer look."
            );
        }

        // skip reply for non-negative comments for now
//        String reply = generateReply(commentText);
        return new CommentModerationResult(false, payload.reason(), "No Reply");
    }

    private SentimentPayload isNegative(String commentText) {
        String prompt = """
                Analyze the sentiment of the comment.
                Return ONLY valid JSON in this exact shape, with no markdown formatting, no code fences, and no additional text:
                {"negative": true/false, "reason": "short reason"}
                
                Comment:
                %s
                """.formatted(commentText);

        ClaudeResponse response = callClaude(
                sentimentModel,
                prompt,
                100,
                0.0
        );

        String text = extractText(response);
        log.info("Sentiment analysis response: {}", text);
        try {
            return objectMapper.readValue(text, SentimentPayload.class);
        } catch (Exception ex) {
            String msg = "Failed to parse sentiment analysis response. Raw response: %s".formatted(text);
            log.warn(msg, ex);
            return new SentimentPayload(false, msg);
        }
    }

    private String generateReply(String commentText) {
        String prompt = """
                Write a concise, friendly, meaningful reply to this user comment.
                The reply should feel human, helpful, and natural.
                
                Comment:
                %s
                """.formatted(commentText);

        ClaudeResponse response = callClaude(
                replyModel,
                prompt,
                120,
                0.7
        );

        return extractText(response);
    }

    private ClaudeResponse callClaude(String model, String prompt, int maxTokens, double temperature) {
        ClaudeRequest request = new ClaudeRequest(
                model,
                maxTokens,
                temperature,
                List.of(new ClaudeMessage("user", prompt))
        );

        return restClient.post()
                .uri("/v1/messages")
                .body(request)
                .retrieve()
                .body(ClaudeResponse.class);
    }

    private String extractText(ClaudeResponse response) {
        if (response == null || response.content() == null || response.content().isEmpty()) {
            return "";
        }
        String text = response.content().get(0).text();
        // Remove Markdown code fence markers
        text = text.replaceAll("```\\w*\\n?", "").trim();
        return text;
    }

    private record SentimentPayload(boolean negative, String reason) {
    }
}
