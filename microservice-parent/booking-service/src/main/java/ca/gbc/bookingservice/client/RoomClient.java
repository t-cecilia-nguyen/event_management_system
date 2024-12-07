package ca.gbc.bookingservice.client;

import groovy.util.logging.Slf4j;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@Slf4j
public interface RoomClient {
    Logger log = LoggerFactory.getLogger(RoomClient.class);

    @GetExchange ("/rooms/{id}/availability")
    @CircuitBreaker(name = "room", fallbackMethod = "fallbackMethod")
    @Retry(name = "room")
    boolean checkRoomAvailability(@PathVariable Long id);

    default boolean fallbackMethod(Long id, Throwable throwable) {
        log.info("Cannot get room {}, failure reason: {}", id, throwable.getMessage());
        log.error("Fallback triggered for room ID {}. Failure reason: {}", id, throwable.getMessage(), throwable);
        return false;
    }
}
