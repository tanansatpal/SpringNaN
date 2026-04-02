package com.example.demo.dto;

public record GreetingResponse(
        String username,
        String greeting,
        double temperature,
        double windSpeed
) {
}
