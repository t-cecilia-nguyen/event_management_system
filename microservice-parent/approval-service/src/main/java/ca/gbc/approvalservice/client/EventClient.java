package ca.gbc.approvalservice.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "event-service", url = "${event.service.url}")
public interface EventClient {

    @GetMapping("/api/event/{eventId}")
    Map<String, Object> getEventById(@PathVariable("eventId") String eventId);

}
