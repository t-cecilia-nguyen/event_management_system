package ca.gbc.eventservice.service;

import ca.gbc.eventservice.dto.EventRequest;
import ca.gbc.eventservice.dto.EventResponse;
import ca.gbc.eventservice.exception.UserRoleException;

import java.util.List;

public interface EventService {
    EventResponse createEvent (Long userId, EventRequest eventRequest) throws UserRoleException;
    List<EventResponse> getAllEvents();
    String updateEvent(String id, EventRequest eventRequest);
    void deleteEvent(String id);
}
