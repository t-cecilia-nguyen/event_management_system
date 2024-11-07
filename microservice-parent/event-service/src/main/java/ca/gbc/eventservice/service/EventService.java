package ca.gbc.eventservice.service;

import ca.gbc.eventservice.dto.EventRequest;
import ca.gbc.eventservice.dto.EventResponse;
import ca.gbc.eventservice.exception.UserIdException;
import ca.gbc.eventservice.exception.UserRoleException;

import java.util.List;

public interface EventService {
    EventResponse createEvent (EventRequest eventRequest) throws UserRoleException, UserIdException;
    List<EventResponse> getAllEvents();
    String updateEvent(String id, EventRequest eventRequest) throws UserIdException, UserRoleException;
    void deleteEvent(String id);
    EventResponse getEventById(String id);
}
