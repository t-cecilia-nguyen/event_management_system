package ca.gbc.eventservice.service;

import ca.gbc.eventservice.dto.EventRequest;
import ca.gbc.eventservice.dto.EventResponse;
import ca.gbc.eventservice.model.Booking;
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


    private Booking getBookingById(String id) {
        String url = "http://booking-service/api/booking/" + id;
        System.out.println("Calling Booking URL: " + url);

        Booking booking = restTemplate.getForObject(url, Booking.class);

        if (booking != null) {
            return booking;
        }
        return null;
    }

    @Override
    public EventResponse createEvent(String bookingId, EventRequest eventRequest) {

        //get booking
        Booking booking = this.getBookingById(bookingId);

        //put together booking and event
        if (booking != null) {
            Event event = Event.builder()
                    .eventName(eventRequest.eventName())
                    .eventType(eventRequest.eventType())
                    .organizerId(booking.getUserId())
                    .bookingId(booking.getBookingId())
                    .expectedAttendees(eventRequest.expectedAttendees())
                    .build();

            eventRepository.save(event);

            log.info("New Event {} is saved", eventRequest.eventName());
            return new EventResponse(
                    event.getId(),
                    event.getEventName(),
                    event.getEventType(),
                    event.getOrganizerId(),
                    event.getBookingId(),
                    event.getExpectedAttendees()
            );
        }

        return null;

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
                event.getBookingId(),
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
