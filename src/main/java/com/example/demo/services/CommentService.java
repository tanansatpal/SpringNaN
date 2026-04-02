package com.example.demo.services;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public CommentResponse create(CommentRequest request, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Creating comment for userId={}", currentUser.getId());

        Comment comment = new Comment();
        comment.setUserId(currentUser.getId());
        comment.setContent(request.content());
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());

        Comment saved = commentRepository.save(comment);
        log.info("Comment created successfully: commentId={} userId={}", saved.getId(), saved.getUserId());
        return toResponse(saved);
    }

    public CommentResponse getById(String id, Authentication authentication) {
        log.info("Fetching comment by id={}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comment not found: id={}", id);
                    return new IllegalArgumentException("Comment not found");
                });

        ensureOwnerOrThrow(comment, authentication);
        return toResponse(comment);
    }

    public List<CommentResponse> getAll(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Fetching all comments for userId={}", currentUser.getId());

        return commentRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CommentResponse> getByUserId(String userId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Fetching comments for requestedUserId={} by currentUserId={}", userId, currentUser.getId());

        if (!currentUser.getId().equals(userId)) {
            log.warn("Access denied while fetching comments: currentUserId={} requestedUserId={}", currentUser.getId(), userId);
            throw new IllegalArgumentException("Access denied");
        }

        return commentRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CommentResponse update(String id, CommentRequest request, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Updating comment id={} by userId={}", id, currentUser.getId());

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comment not found for update: id={}", id);
                    return new IllegalArgumentException("Comment not found");
                });

        ensureOwnerOrThrow(comment, authentication);

        comment.setUserId(currentUser.getId());
        comment.setContent(request.content());
        comment.setUpdatedAt(Instant.now());

        Comment saved = commentRepository.save(comment);
        log.info("Comment updated successfully: commentId={} userId={}", saved.getId(), saved.getUserId());
        return toResponse(saved);
    }

    public void delete(String id, Authentication authentication) {
        log.info("Deleting comment id={}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Comment not found for delete: id={}", id);
                    return new IllegalArgumentException("Comment not found");
                });

        ensureOwnerOrThrow(comment, authentication);
        commentRepository.deleteById(id);
        log.info("Comment deleted successfully: id={}", id);
    }

    private void ensureOwnerOrThrow(Comment comment, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        if (!comment.getUserId().equals(currentUser.getId())) {
            log.warn("Access denied: commentOwnerId={} currentUserId={}", comment.getUserId(), currentUser.getId());
            throw new IllegalArgumentException("Access denied");
        }
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
