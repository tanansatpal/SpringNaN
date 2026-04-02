package com.example.demo.dto;

import com.example.demo.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record BookingResponse(
        String id,
        String propertyId,
        String ownerId,
        String guestId,
        LocalDate checkIn,
        LocalDate checkOut,
        BigDecimal totalPrice,
        BookingStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
