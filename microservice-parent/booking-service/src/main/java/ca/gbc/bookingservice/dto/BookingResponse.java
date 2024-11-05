package ca.gbc.bookingservice.dto;

import java.time.LocalDateTime;

public record BookingResponse(
        String id,
        String userId,
        Long roomId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String purpose
) {}
