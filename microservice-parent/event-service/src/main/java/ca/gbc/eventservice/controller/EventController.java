package ca.gbc.eventservice.controller;


import ca.gbc.eventservice.dto.EventRequest;
import ca.gbc.eventservice.dto.EventResponse;
import ca.gbc.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/{bookingId}")
    public ResponseEntity<EventResponse> createEvent(@PathVariable("bookingId") String bookingId, @RequestBody EventRequest eventRequest) {
        EventResponse eventResponse = eventService.createEvent(bookingId, eventRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location" , "/api/event/" + eventResponse.id());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(eventResponse);
    }


    @GetMapping
    public List<EventResponse> getAllEvents() { return eventService.getAllEvents(); }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") String eventId,
                                         @RequestBody EventRequest eventRequest) {

        String updatedId = eventService.updateEvent(eventId, eventRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location" , "/api/event/" + updatedId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .headers(headers)
                .body(updatedId);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable("eventId") String eventId) {

        eventService.deleteEvent(eventId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
