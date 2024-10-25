package ca.gbc.bookingservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable("bookingId") String bookingId) {

        Booking newBooking = Booking.builder()
                .bookingId(bookingId)
                .roomId("R123")
                .userId("USER123")
                .build();

        return ResponseEntity.ok(newBooking);
    }
}
