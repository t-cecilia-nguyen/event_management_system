package ca.gbc.eventservice.service;

import ca.gbc.eventservice.client.UserClient;
import ca.gbc.eventservice.dto.EventRequest;
import ca.gbc.eventservice.dto.EventResponse;
import ca.gbc.eventservice.exception.UserIdException;
import ca.gbc.eventservice.exception.UserRoleException;
import ca.gbc.eventservice.model.Event;
import ca.gbc.eventservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;
    private final MongoTemplate mongoTemplate;
    private final UserClient userClient ;


    private void checkUserRole(EventRequest eventRequest) throws UserRoleException {
        //check userRole
        Long organizerId = eventRequest.organizerId();

        var userRole = userClient.checkUserRole(organizerId);

        if (userRole == null) {
            throw new UserRoleException("unknown", "User role not recognized or not authorized to create events.");
        }

        switch (userRole.toLowerCase()) {
            case "student":
                if (eventRequest.expectedAttendees() > 100) {
                    throw new UserRoleException(userRole, "Cannot create event with more than 100 attendees.");
                }
                break;
            case "faculty":
                if (eventRequest.expectedAttendees() > 500) {
                    throw new UserRoleException(userRole, "Cannot create event with more than 500 attendees.");
                }
                break;
            case "staff":
                if (eventRequest.expectedAttendees() > 200) {
                    throw new UserRoleException(userRole, "Cannot create event with more than 200 attendees.");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public EventResponse createEvent( EventRequest eventRequest) throws UserRoleException, UserIdException {

        //check organizerId

        Long organizerId = eventRequest.organizerId();

        log.info("Checking if organizer ID exists: " + organizerId);

        if(!userClient.isUserExist(organizerId)){ throw new UserIdException(eventRequest.organizerId(), "User Id: "+eventRequest.organizerId()+ " is not recognized."); }

        //check role
        checkUserRole(eventRequest);


        Event event = Event.builder()
                .eventName(eventRequest.eventName())
                .eventType(eventRequest.eventType())
                .organizerId(eventRequest.organizerId())
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
    public String updateEvent(String eventId, EventRequest eventRequest) throws UserIdException, UserRoleException {

        log.debug("Update Event with Id {}", eventId);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(eventId));

        Event event = mongoTemplate.findOne(query, Event.class);

        if(event != null){
            event.setEventName(eventRequest.eventName());
            event.setEventType(eventRequest.eventType());

            if(userClient.isUserExist(eventRequest.organizerId())){
                event.setOrganizerId(eventRequest.organizerId());
            }else{
                throw new UserIdException(eventRequest.organizerId(), "organizerId is not recognized");
            }

            //check Role
            checkUserRole(eventRequest);

            event.setExpectedAttendees(eventRequest.expectedAttendees());

            log.info("Event with Id {} is updated", eventId);
            return eventRepository.save(event).getId();
        }
        return eventId;
    }

    @Override
    public void deleteEvent(String eventId) {
        log.debug("Delete Event with Id {}", eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        eventRepository.deleteById(eventId);
    }

    @Override
    public EventResponse getEventById(String id) {
        Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return mapToEventResponse(event);
    }


}
