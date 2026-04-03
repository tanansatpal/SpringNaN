package com.example.demo.services;

import com.example.demo.dto.BookingRequest;
import com.example.demo.dto.BookingResponse;
import com.example.demo.entity.Property;
import com.example.demo.entity.PropertyBooking;
import com.example.demo.entity.User;
import com.example.demo.repository.PropertyBookingRepository;
import com.example.demo.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyBookingRepository bookingRepository;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(propertyRepository, bookingRepository);
    }

    @Test
    void create_shouldCreateBookingSuccessfully() {
        User user = new User();
        user.setId("user-1");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        Property property = new Property();
        property.setId("prop-1");
        property.setOwnerId("owner-1");
        property.setActive(true);
        property.setPricePerNight(new BigDecimal("100.00"));

        BookingRequest request = new BookingRequest(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

        when(propertyRepository.findById("prop-1")).thenReturn(Optional.of(property));
        when(bookingRepository.existsByPropertyIdAndStatusInAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                eq("prop-1"),
                anyList(),
                any(), any()
        )).thenReturn(false);

        when(bookingRepository.save(any(PropertyBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService.create("prop-1", request, authentication);

        assertNotNull(response);
        assertEquals("prop-1", response.propertyId());
        assertEquals("user-1", response.guestId());
        assertEquals(new BigDecimal("200.00"), response.totalPrice());
    }

    @Test
    void create_shouldThrowWhenCheckoutIsInvalid() {
        User user = new User();
        user.setId("user-1");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        BookingRequest request = new BookingRequest(LocalDate.now().plusDays(3), LocalDate.now().plusDays(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.create("prop-1", request, authentication));

        assertEquals("Checkout must be after check-in", ex.getMessage());
    }

    @Test
    void create_shouldThrowWhenPropertyNotFound() {
        User user = new User();
        user.setId("user-1");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        BookingRequest request = new BookingRequest(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        when(propertyRepository.findById("prop-1")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.create("prop-1", request, authentication));

        assertEquals("Property not found", ex.getMessage());
    }

    @Test
    void create_shouldThrowWhenPropertyIsInactive() {
        User user = new User();
        user.setId("user-1");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        Property property = new Property();
        property.setId("prop-1");
        property.setOwnerId("owner-1");
        property.setActive(false);
        property.setPricePerNight(new BigDecimal("100.00"));

        BookingRequest request = new BookingRequest(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        when(propertyRepository.findById("prop-1")).thenReturn(Optional.of(property));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.create("prop-1", request, authentication));

        assertEquals("Property is not available", ex.getMessage());
    }

    @Test
    void create_shouldThrowWhenOwnerBooksOwnProperty() {
        User user = new User();
        user.setId("owner-1");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        Property property = new Property();
        property.setId("prop-1");
        property.setOwnerId("owner-1");
        property.setActive(true);
        property.setPricePerNight(new BigDecimal("100.00"));

        BookingRequest request = new BookingRequest(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        when(propertyRepository.findById("prop-1")).thenReturn(Optional.of(property));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.create("prop-1", request, authentication));

        assertEquals("Owner cannot book their own property", ex.getMessage());
    }

    @Test
    void create_shouldThrowWhenOverlapExists() {
        User user = new User();
        user.setId("user-1");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        Property property = new Property();
        property.setId("prop-1");
        property.setOwnerId("owner-1");
        property.setActive(true);
        property.setPricePerNight(new BigDecimal("100.00"));

        BookingRequest request = new BookingRequest(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

        when(propertyRepository.findById("prop-1")).thenReturn(Optional.of(property));
        when(bookingRepository.existsByPropertyIdAndStatusInAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                eq("prop-1"), anyList(), any(), any()
        )).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.create("prop-1", request, authentication));

        assertEquals("Property is already booked for the selected dates", ex.getMessage());
    }

    @Test
    void getMyBookings_shouldReturnUserBookings() {
        User user = new User();
        user.setId("user-1");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        PropertyBooking booking = new PropertyBooking();
        booking.setPropertyId("prop-1");
        booking.setGuestId("user-1");
        booking.setOwnerId("owner-1");

        when(bookingRepository.findByGuestId("user-1")).thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getMyBookings(authentication);

        assertEquals(1, result.size());
        assertEquals("prop-1", result.get(0).propertyId());
    }

    @Test
    void getBookingsForMyProperty_shouldReturnBookingsForOwner() {
        User user = new User();
        user.setId("owner-1");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        Property property = new Property();
        property.setId("prop-1");
        property.setOwnerId("owner-1");

        PropertyBooking booking = new PropertyBooking();
        booking.setPropertyId("prop-1");
        booking.setGuestId("user-1");
        booking.setOwnerId("owner-1");

        when(propertyRepository.findById("prop-1")).thenReturn(Optional.of(property));
        when(bookingRepository.findByPropertyIdAndStatusIn(eq("prop-1"), anyList())).thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getBookingsForMyProperty("prop-1", authentication);

        assertEquals(1, result.size());
        assertEquals("prop-1", result.get(0).propertyId());
    }

    @Test
    void getBookingsForMyProperty_shouldThrowWhenAccessDenied() {
        User user = new User();
        user.setId("user-1");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        Property property = new Property();
        property.setId("prop-1");
        property.setOwnerId("owner-1");

        when(propertyRepository.findById("prop-1")).thenReturn(Optional.of(property));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getBookingsForMyProperty("prop-1", authentication));

        assertEquals("Access denied", ex.getMessage());
    }
}
