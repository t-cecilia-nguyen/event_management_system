package ca.gbc.eventservice.model;

import ca.gbc.eventservice.dto.EventRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(value="events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {

    @Id
    private String id;
    private String eventName;
    private String eventType;

    private Long organizerId;
    private Integer expectedAttendees;

}
