package com.example.demo.controllers;

import com.example.demo.dto.BookingRequest;
import com.example.demo.dto.BookingResponse;
import com.example.demo.services.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingControllerTest {

    @Test
    void create_shouldDelegateToService() {
        BookingService bookingService = mock(BookingService.class);
        BookingController controller = new BookingController(bookingService);

        Authentication authentication = mock(Authentication.class);
        BookingRequest request = new BookingRequest(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        BookingResponse expected = new BookingResponse(
                "b1", "p1", "o1", "g1",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                new BigDecimal("100.00"),
                null,
                Instant.now(),
                Instant.now()
        );

        when(bookingService.create("p1", request, authentication)).thenReturn(expected);

        ResponseEntity<BookingResponse> response = controller.create("p1", request, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expected, response.getBody());
    }

    @Test
    void getMyBookings_shouldDelegateToService() {
        BookingService bookingService = mock(BookingService.class);
        BookingController controller = new BookingController(bookingService);

        Authentication authentication = mock(Authentication.class);
        when(bookingService.getMyBookings(authentication)).thenReturn(List.of());

        ResponseEntity<List<BookingResponse>> response = controller.getMyBookings(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getBookingsForProperty_shouldDelegateToService() {
        BookingService bookingService = mock(BookingService.class);
        BookingController controller = new BookingController(bookingService);

        Authentication authentication = mock(Authentication.class);
        when(bookingService.getBookingsForMyProperty("p1", authentication)).thenReturn(List.of());

        ResponseEntity<List<BookingResponse>> response = controller.getBookingsForProperty("p1", authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}
