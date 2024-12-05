package ca.gbc.bookingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingPlacedEvent {

    private String bookingId;
    private String userId;
    private Long roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
}
