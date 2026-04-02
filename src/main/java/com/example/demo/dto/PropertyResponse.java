package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PropertyResponse(
        String id,
        String ownerId,
        String title,
        String description,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String country,
        String postalCode,
        Double latitude,
        Double longitude,
        BigDecimal pricePerNight,
        Integer maxGuests,
        Integer bedrooms,
        Integer bathrooms,
        List<String> amenities,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
