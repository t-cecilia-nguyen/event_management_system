package ca.gbc.eventservice.dto;

import java.time.LocalDateTime;

public record EventResponse(
        String id,
        String eventName,
        String eventType,
        Long organizerId,
        Integer expectedAttendees
) {

}
