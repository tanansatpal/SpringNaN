package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingRequest(
        @NotNull LocalDate checkIn,
        @NotNull LocalDate checkOut
) {
}
