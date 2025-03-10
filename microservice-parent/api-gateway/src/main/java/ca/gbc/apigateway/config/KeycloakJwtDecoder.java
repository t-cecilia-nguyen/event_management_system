package ca.gbc.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class KeycloakJwtDecoder implements JwtDecoder {
    private final NimbusJwtDecoder delegate;

    public KeycloakJwtDecoder(String jwkSetUri, String issuerUri) {
        this.delegate = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        this.delegate.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuerUri));
    }

    @Override
    public Jwt decode(String token) {
        log.info("Decoding JWT token: {}", token);
        Jwt jwt = delegate.decode(token);

        // Extract authorities
        List<SimpleGrantedAuthority> authorities = extractAuthorities(jwt);
        log.info("Extracted Authorities: {}", authorities);

        // Return a modified JWT with authorities claim
        return Jwt.withTokenValue(jwt.getTokenValue())
                .headers(headers -> headers.putAll(jwt.getHeaders()))
                .claims(claims -> {
                    claims.putAll(jwt.getClaims());
                    claims.put("authorities", authorities);
                })
                .build();
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess == null || realmAccess.get("roles") == null) {
            log.warn("No roles found in realm_access");
            return List.of();
        }

        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
