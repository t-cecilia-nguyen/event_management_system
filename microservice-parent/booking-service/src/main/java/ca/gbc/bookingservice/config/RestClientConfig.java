package ca.gbc.bookingservice.config;

import ca.gbc.bookingservice.client.RoomClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Value("${room.service.url}")
    private String roomServiceUrl;

    @Bean
    public RoomClient roomClient() {

        RestClient restClient = RestClient.builder()
                .baseUrl(roomServiceUrl)
                .build();

        var restClientAdapter = RestClientAdapter.create(restClient);
        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(RoomClient.class);
    }
}
