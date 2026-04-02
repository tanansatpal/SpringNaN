package com.example.demo.repository;

import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.PropertyBooking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface PropertyBookingRepository extends MongoRepository<PropertyBooking, String> {
    List<PropertyBooking> findByPropertyIdAndStatusIn(String propertyId, List<BookingStatus> statuses);

    boolean existsByPropertyIdAndStatusInAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
            String propertyId,
            List<BookingStatus> statuses,
            LocalDate checkIn,
            LocalDate checkOut
    );

    List<PropertyBooking> findByGuestId(String guestId);
}
