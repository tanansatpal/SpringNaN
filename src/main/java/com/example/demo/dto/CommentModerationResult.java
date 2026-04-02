package com.example.demo.dto;

public record CommentModerationResult(
        boolean negative,
        String reason,
        String reply
) {
}
