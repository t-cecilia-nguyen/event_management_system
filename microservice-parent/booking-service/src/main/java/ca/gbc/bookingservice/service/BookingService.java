package ca.gbc.bookingservice.service;

import ca.gbc.bookingservice.dto.BookingRequest;
import ca.gbc.bookingservice.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest bookingRequest);
    List<BookingResponse> getAllBookings();
    BookingResponse getBookingById(String bookingId);
    String updateBooking(String bookingId, BookingRequest bookingRequest);
    void deleteBooking(String bookingId);
}
