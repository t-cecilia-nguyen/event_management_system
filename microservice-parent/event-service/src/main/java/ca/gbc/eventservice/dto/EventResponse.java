package ca.gbc.eventservice.dto;

public record EventResponse(
        String id,
        String eventName,
        String eventType,
        Long organizerId,
        Integer expectedAttendees
) { }
