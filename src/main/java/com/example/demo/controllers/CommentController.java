package com.example.demo.controllers;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.services.CommentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        log.info("Create comment request received");
        return ResponseEntity.ok(commentService.create(request, authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getById(
            @PathVariable String id,
            Authentication authentication
    ) {
        log.info("Get comment by id request received: id={}", id);
        return ResponseEntity.ok(commentService.getById(id, authentication));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAll(Authentication authentication) {
        log.info("Get all comments request received");
        return ResponseEntity.ok(commentService.getAll(authentication));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> getByUserId(
            @PathVariable String userId,
            Authentication authentication
    ) {
        log.info("Get comments by userId request received: userId={}", userId);
        return ResponseEntity.ok(commentService.getByUserId(userId, authentication));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        log.info("Update comment request received: id={}", id);
        return ResponseEntity.ok(commentService.update(id, request, authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        log.info("Delete comment request received: id={}", id);
        commentService.delete(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
