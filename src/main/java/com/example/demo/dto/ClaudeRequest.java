package com.example.demo.dto;

import java.util.List;

public record ClaudeRequest(
        String model,
        int max_tokens,
        double temperature,
        List<ClaudeMessage> messages
) {
}
