package ca.gbc.approvalservice.client;


import groovy.util.logging.Slf4j;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.service.annotation.GetExchange;

@Slf4j
public interface UserClient {

    Logger log = LoggerFactory.getLogger(UserClient.class);

    @GetExchange("/users/{id}/usertype")
    @CircuitBreaker(name = "user", fallbackMethod = "fallbackMethod")
    @Retry(name = "user")
    String checkUserType(@PathVariable("id") String id);

    default boolean fallbackMethod(String id, Throwable throwable) {
        log.info("Cannot get user with id {}, failure reason: {}", id, throwable.getMessage());
        return false;
    }
}
