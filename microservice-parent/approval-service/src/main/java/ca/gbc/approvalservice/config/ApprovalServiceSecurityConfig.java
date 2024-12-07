package ca.gbc.approvalservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enables @PreAuthorize for method-level security
public class ApprovalServiceSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF if not required
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/debug-token").permitAll()  // Allow access to debug endpoint
                        .requestMatchers("/api/approval/approve").hasRole("staff")
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(keycloakJwtDecoder())) // Use KeycloakJwtDecoder
                );
        return http.build();
    }

    @Bean
    public JwtDecoder keycloakJwtDecoder() {
        // Return the custom KeycloakJwtDecoder for decoding JWTs
        String jwkSetUri = "http://keycloak:8080/realms/booking-event-security-realm/protocol/openid-connect/certs";
        String issuerUri = "http://keycloak:8080/realms/booking-event-security-realm";
        return new KeycloakJwtDecoder(jwkSetUri, issuerUri);
    }
}
