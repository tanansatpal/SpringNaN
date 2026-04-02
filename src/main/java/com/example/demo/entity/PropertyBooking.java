package com.example.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Document(collection = "property_bookings")
public class PropertyBooking {
    @Id
    private String id;

    private String propertyId;
    private String ownerId;
    private String guestId;

    private LocalDate checkIn;
    private LocalDate checkOut;

    private BigDecimal totalPrice;

    private BookingStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
