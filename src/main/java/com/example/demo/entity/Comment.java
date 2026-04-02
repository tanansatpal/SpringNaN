package com.example.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;

    private String userId;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean flaggedForReview;
    private String moderationReason;
    private String aiReply;
}
