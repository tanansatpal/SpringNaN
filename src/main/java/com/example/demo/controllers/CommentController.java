package com.example.demo.controllers;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponse> create(@Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(commentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAll() {
        return ResponseEntity.ok(commentService.getAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(commentService.getByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable String id,
            @Valid @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
