package com.example.demo.services;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public CommentResponse create(CommentRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Comment comment = new Comment();
        comment.setUserId(user.getId());
        comment.setContent(request.content());
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());

        Comment saved = commentRepository.save(comment);
        return toResponse(saved);
    }

    public CommentResponse getById(String id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        return toResponse(comment);
    }

    public List<CommentResponse> getAll() {
        return commentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CommentResponse> getByUserId(String userId) {
        return commentRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CommentResponse update(String id, CommentRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.setUserId(user.getId());
        comment.setContent(request.content());
        comment.setUpdatedAt(Instant.now());

        return toResponse(commentRepository.save(comment));
    }

    public void delete(String id) {
        if (!commentRepository.existsById(id)) {
            throw new IllegalArgumentException("Comment not found");
        }
        commentRepository.deleteById(id);
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getUserId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
