package com.asc.politicalscorecard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize, @PostAuthorize, @Secured
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // Allow all OPTIONS requests for CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Everything else is public by default.
                // Use @PreAuthorize on individual methods to require auth/roles.
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    /**
     * Maps Keycloak's realm roles onto Spring's ROLE_* authorities.
     *
     * NOT `JwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(...)`
     * — that converter only reads a FLAT top-level claim; it does NOT
     * traverse dot-notation into a nested object, so the previous
     * `setAuthoritiesClaimName("realm_access.roles")` was silently a
     * no-op (Keycloak's roles live under a NESTED "realm_access":
     * {"roles": [...]} claim, not a literal claim key containing a
     * dot). Confirmed live 2026-07-14 while adding the first REAL
     * hasRole() check this app ever exercised
     * (PolicyVoteSubmissionController) — every `hasRole(...)` in this
     * codebase (including the pre-existing OidcTestController demo
     * endpoints) has never actually granted a role-based authority
     * until this fix, since none of them were tested against a live
     * role-bearing token before. Mirrors the exact nested-claim
     * extraction UserInfoService.extractRoles() already does
     * correctly for its own (non-Spring-Security) purposes.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(this::realmRoleAuthorities);
        return jwtAuthenticationConverter;
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> realmRoleAuthorities(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?> roles) {
            for (Object role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }
        return authorities;
    }
}
