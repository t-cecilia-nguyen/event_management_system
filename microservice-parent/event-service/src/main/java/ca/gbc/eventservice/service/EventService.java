package ca.gbc.eventservice.service;

import ca.gbc.eventservice.dto.EventRequest;
import ca.gbc.eventservice.dto.EventResponse;
import ca.gbc.eventservice.model.Booking;
import ca.gbc.eventservice.model.Event;

import java.util.List;

public interface EventService {
    EventResponse createEvent (String bookingId, EventRequest eventRequest);
    List<EventResponse> getAllEvents();
    String updateEvent(String id, EventRequest eventRequest);
    void deleteEvent(String id);
}
