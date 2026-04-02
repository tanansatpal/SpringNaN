package com.example.demo.controllers;

import com.example.demo.dto.BookingRequest;
import com.example.demo.dto.BookingResponse;
import com.example.demo.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/property/{propertyId}")
    public ResponseEntity<BookingResponse> create(
            @PathVariable String propertyId,
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookingService.create(propertyId, request, authentication));
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getMyBookings(authentication));
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<BookingResponse>> getBookingsForProperty(
            @PathVariable String propertyId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookingService.getBookingsForMyProperty(propertyId, authentication));
    }
}
