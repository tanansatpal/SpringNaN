package com.example.demo.dto;

public record AuthResponse(
        boolean success,
        String message,
        String username,
        String email,
        String token
) {
}
