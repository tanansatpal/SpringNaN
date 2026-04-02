package com.example.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "properties")
public class Property {
    @Id
    private String id;

    private String ownerId;
    private String title;
    private String description;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    private Double latitude;
    private Double longitude;

    private BigDecimal pricePerNight;
    private Integer maxGuests;
    private Integer bedrooms;
    private Integer bathrooms;

    private List<String> amenities;

    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
