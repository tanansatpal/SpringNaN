package com.example.demo.services;

import com.example.demo.dto.BookingRequest;
import com.example.demo.dto.BookingResponse;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.Property;
import com.example.demo.entity.PropertyBooking;
import com.example.demo.entity.User;
import com.example.demo.repository.PropertyBookingRepository;
import com.example.demo.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final PropertyRepository propertyRepository;
    private final PropertyBookingRepository bookingRepository;

    public BookingService(PropertyRepository propertyRepository, PropertyBookingRepository bookingRepository) {
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
    }

    public BookingResponse create(String propertyId, BookingRequest request, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        if (request.checkOut().isBefore(request.checkIn()) || request.checkOut().isEqual(request.checkIn())) {
            throw new IllegalArgumentException("Checkout must be after check-in");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (!property.isActive()) {
            throw new IllegalArgumentException("Property is not available");
        }

        if (property.getOwnerId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Owner cannot book their own property");
        }

        boolean overlapExists = bookingRepository
                .existsByPropertyIdAndStatusInAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                        propertyId,
                        List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED),
                        request.checkOut().minusDays(1),
                        request.checkIn().plusDays(1)
                );

        if (overlapExists) {
            throw new IllegalArgumentException("Property is already booked for the selected dates");
        }

        PropertyBooking booking = new PropertyBooking();
        booking.setPropertyId(property.getId());
        booking.setOwnerId(property.getOwnerId());
        booking.setGuestId(currentUser.getId());
        booking.setCheckIn(request.checkIn());
        booking.setCheckOut(request.checkOut());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());
        booking.setTotalPrice(calculateTotal(property.getPricePerNight(), request.checkIn(), request.checkOut()));

        PropertyBooking saved = bookingRepository.save(booking);
        return toResponse(saved);
    }

    public List<BookingResponse> getMyBookings(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return bookingRepository.findByGuestId(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BookingResponse> getBookingsForMyProperty(String propertyId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        if (!property.getOwnerId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Access denied");
        }

        return bookingRepository.findByPropertyIdAndStatusIn(
                        propertyId,
                        List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private BigDecimal calculateTotal(BigDecimal pricePerNight, java.time.LocalDate checkIn, java.time.LocalDate checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        return pricePerNight.multiply(BigDecimal.valueOf(nights)).setScale(2, RoundingMode.HALF_UP);
    }

    private BookingResponse toResponse(PropertyBooking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getPropertyId(),
                booking.getOwnerId(),
                booking.getGuestId(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }
}
