package ca.gbc.eventservice.dto;

public record EventRequest(
        String id,
        String eventName,
        String eventType,
        Long organizerId,
        Integer expectedAttendees
) { }
