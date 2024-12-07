package ca.gbc.eventservice.dto;

import java.time.LocalDateTime;

public record EventRequest(
        String id,
        String eventName,
        String eventType,
        Long organizerId,
        Integer expectedAttendees
) {

}
