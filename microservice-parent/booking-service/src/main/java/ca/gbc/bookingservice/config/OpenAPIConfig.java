package ca.gbc.bookingservice.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Value("${booking-service.version}")
    private String version;
    @Bean
    public OpenAPI bookingServiceAPI(){
        return new OpenAPI()
                .info(new Info().title("Booking Service API")
                        .description("This is the REST API for Booking Service")
                        .version(version)
                        .license(new License().name("Apache 2.0"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Booking Service Wiki Doccummentation")
                        .url("https://mycompany.com/booking-service/docs")
                );
    }

}
