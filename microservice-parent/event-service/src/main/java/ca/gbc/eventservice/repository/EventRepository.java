package ca.gbc.eventservice.repository;

import ca.gbc.eventservice.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, String> {
}
