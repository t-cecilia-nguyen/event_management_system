package ca.gbc.eventservice.dto;

public record EventRequest(
        String id,
        String eventName,
        String eventType,
        String organizerId,
        String bookingId,
        Integer expectedAttendees
) { }
