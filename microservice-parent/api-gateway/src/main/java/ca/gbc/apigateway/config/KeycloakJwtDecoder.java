package ca.gbc.apigateway.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakJwtDecoder implements JwtDecoder {

    private final NimbusJwtDecoder delegate;

    public KeycloakJwtDecoder(String jwkSetUri, String issuerUri) {
        this.delegate = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        this.delegate.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuerUri));
    }

    @Override
    public Jwt decode(String token) {
        Jwt jwt = delegate.decode(token);
        List<SimpleGrantedAuthority> authorities = extractAuthorities(jwt);

        // Fix for the headers: Use a Consumer for the headers
        // Fix for the claims: Use a Consumer for the claims
        return Jwt.withTokenValue(jwt.getTokenValue())
                .headers(headers -> headers.putAll(jwt.getHeaders()))  // Use a Consumer to modify headers
                .claims(claims -> claims.putAll(jwt.getClaims()))      // Use a Consumer to modify claims
                .claim("authorities", authorities)  // Add authorities to JWT
                .build();
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        List<String> roles = (List<String>) realmAccess.get("roles");

        if (roles == null) {
            return List.of();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
