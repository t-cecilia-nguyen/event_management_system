package ca.gbc.eventservice.controller;


import ca.gbc.eventservice.dto.EventRequest;
import ca.gbc.eventservice.dto.EventResponse;
import ca.gbc.eventservice.exception.UserIdException;
import ca.gbc.eventservice.exception.UserRoleException;
import ca.gbc.eventservice.model.UserIdErrorResponse;
import ca.gbc.eventservice.model.RoleErrorResponse;
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

    @PostMapping
    public ResponseEntity<?> createEvent( @RequestBody EventRequest eventRequest) {


        try {
            System.out.println("Creating event with request: " + eventRequest);
            EventResponse eventResponse = eventService.createEvent(eventRequest);
            System.out.println("Event created successfully with ID: " + eventResponse.id());


            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/api/event/" + eventResponse.id());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(eventResponse);
        }catch(UserIdException e){
            System.out.println(new UserIdErrorResponse(e.getUserId(), e.getErrorMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserIdErrorResponse(e.getUserId(), e.getErrorMessage()));
        } catch (UserRoleException e) { //400
            System.out.println(new RoleErrorResponse(e.getRole(), e.getErrorMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoleErrorResponse(e.getRole(), e.getErrorMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RoleErrorResponse("internal_error", "An unexpected error occurred."));
        }


    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponse> getAllEvents() { return eventService.getAllEvents(); }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable("eventId") String eventId,
                                         @RequestBody EventRequest eventRequest){
        String updatedId;
        try {
            updatedId = eventService.updateEvent(eventId, eventRequest);
        } catch (UserIdException e) {
            System.out.println(new UserIdErrorResponse(e.getUserId(), e.getErrorMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserIdErrorResponse(e.getUserId(), e.getErrorMessage()));
        }catch (UserRoleException e){
            System.out.println(new RoleErrorResponse(e.getRole(), e.getErrorMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RoleErrorResponse(e.getRole(), e.getErrorMessage()));
        }

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


    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable("eventId") String eventId){
        EventResponse event = eventService.getEventById(eventId);
        return ResponseEntity.ok(event);
    }


}
