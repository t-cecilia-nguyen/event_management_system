package ca.gbc.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;


@Slf4j
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize support
public class SecurityConfig {

    private final String[] noauthResourceUris = {
            "/swagger-ui",
            "/swagger-ui/*",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/aggregate/**",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Initializing Security Filter Chain...");

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(noauthResourceUris).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(keycloakJwtDecoder()))  // Use custom KeycloakJwtDecoder
                )
                .build();
    }

    @Bean
    public JwtDecoder keycloakJwtDecoder() {
        String jwkSetUri = "http://keycloak:8080/realms/booking-event-security-realm/protocol/openid-connect/certs";
        String issuerUri = "http://keycloak:8080/realms/booking-event-security-realm";
        return new KeycloakJwtDecoder(jwkSetUri, issuerUri);
    }
}
