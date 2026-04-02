package com.example.demo.dto;

import java.time.Instant;

public record CommentResponse(
        String id,
        String userId,
        String content,
        Instant createdAt,
        Instant updatedAt,
        boolean flaggedForReview,
        String moderationReason,
        String aiReply
) {
}
