package com.example.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record PropertyRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String addressLine1,
        String addressLine2,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String country,
        @NotBlank String postalCode,
        Double latitude,
        Double longitude,
        @NotNull @DecimalMin("0.0") BigDecimal pricePerNight,
        @NotNull Integer maxGuests,
        Integer bedrooms,
        Integer bathrooms,
        List<String> amenities
) {
}
