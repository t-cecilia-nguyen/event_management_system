package ca.gbc.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

        // Extract roles and convert them to GrantedAuthorities
        List<SimpleGrantedAuthority> authorities = extractAuthorities(jwt);
        log.info("Extracted Authorities in approval: {}", authorities);

        // Add the authorities to the SecurityContextHolder
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(jwt.getSubject(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Return the JWT token with authorities in the claims
        return Jwt.withTokenValue(jwt.getTokenValue())
                .headers(headers -> headers.putAll(jwt.getHeaders()))
                .claims(claims -> {
                    claims.putAll(jwt.getClaims());
                    claims.put("authorities", authorities); // Add authorities to claims
                })
                .build();
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
        // Extract roles from "realm_access" in JWT claims
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess == null) {
            return List.of(); // No roles found
        }

        List<String> roles = (List<String>) realmAccess.get("roles");
        if (roles == null) {
            return List.of();
        }

        // Convert roles to GrantedAuthorities with "ROLE_" prefix
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
