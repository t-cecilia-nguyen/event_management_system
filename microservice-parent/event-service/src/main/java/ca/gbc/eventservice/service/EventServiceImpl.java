package ca.gbc.eventservice.service;

import ca.gbc.eventservice.dto.EventRequest;
import ca.gbc.eventservice.dto.EventResponse;
import ca.gbc.eventservice.exception.UserRoleException;
import ca.gbc.eventservice.model.Event;
import ca.gbc.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;
    private final MongoTemplate mongoTemplate;
    private final RestTemplate restTemplate;


    private String checkUserRole(Long id) {
        String url = "http://user-service/users/" + id +"/role";
        System.out.println("Calling User-Service URL: " + url);

        String userRole = restTemplate.getForObject(url, String.class);

        if (userRole != null) {
            return userRole;
        }
        return null;
    }

    @Override
    public EventResponse createEvent(Long userid, EventRequest eventRequest) throws UserRoleException {

        //get User
        String userRole = this.checkUserRole(userid);

        if (userRole == null) {
            throw new UserRoleException("unknown", "User role not recognized or not authorized to create events.");
        }

        //check userRole and before creating event
        switch (userRole.toLowerCase()) {
            case "student":
                if (eventRequest.expectedAttendees() >= 100) {
                    throw new UserRoleException(userRole, "Cannot create event with more than 100 attendees.");
                }
                break;
            case "faculty":
                if (eventRequest.expectedAttendees() >= 500) {
                    throw new UserRoleException(userRole, "Cannot create event with more than 500 attendees.");
                }
                break;
            case "staff":
                if (eventRequest.expectedAttendees() >= 50) {
                    throw new UserRoleException(userRole, "Cannot create event with more than 50 attendees.");
                }
                break;
            default:
                break;
        }

        Event event = Event.builder()
                .eventName(eventRequest.eventName())
                .eventType(eventRequest.eventType())
                .organizerId(userid)
                .expectedAttendees(eventRequest.expectedAttendees())
                .build();

        eventRepository.save(event);

        log.info("New Event {} is saved", eventRequest.eventName());

        return new EventResponse(
                event.getId(),
                event.getEventName(),
                event.getEventType(),
                event.getOrganizerId(),
                event.getExpectedAttendees()
        );

    }

    @Override
    public List<EventResponse> getAllEvents() {
        log.debug("Get all the events");

        List<Event> events = eventRepository.findAll();

        return events.stream().map(this::mapToEventResponse).toList();
    }

    private EventResponse mapToEventResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getEventName(),
                event.getEventType(),
                event.getOrganizerId(),
                event.getExpectedAttendees()
        );
    }


    @Override
    public String updateEvent(String eventId, EventRequest eventRequest) {

        log.debug("Update Event with Id {}", eventId);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(eventId));

        Event event = mongoTemplate.findOne(query, Event.class);

        if(event != null){
            event.setEventName(eventRequest.eventName());
            event.setEventType(eventRequest.eventType());
            //event.setOrganizerId(eventRequest.organizerId());
            event.setExpectedAttendees(eventRequest.expectedAttendees());

            log.info("Event with Id {} is updated", eventId);
            return eventRepository.save(event).getId();
        }
        return eventId;
    }

    @Override
    public void deleteEvent(String eventId) {
        log.debug("Delete Event with Id {}", eventId);

        eventRepository.deleteById(eventId);
    }



}
