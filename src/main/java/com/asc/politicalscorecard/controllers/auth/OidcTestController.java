package com.asc.politicalscorecard.controllers.auth;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * OIDC test controller — verifies that OAuth2 resource server, JWT validation,
 * and role-based access control are working correctly.
 *
 * Uses @PreAuthorize and @AuthenticationPrincipal instead of SecurityConfiguration
 * URL matchers, so security lives directly on the method.
 *
 * Endpoint summary:
 *   GET /auth/test                  — No auth. Base existence check.
 *   GET /auth/test/public           — No auth. Always works.
 *   GET /auth/test/inspect          — No auth. Shows whatever auth state exists.
 *   GET /auth/test/secure           — Requires valid JWT.
 *   GET /auth/test/role-admin       — Requires ROLE_admin.
 *   GET /auth/test/role-moderator   — Requires ROLE_moderator.
 *   GET /auth/test/role-user        — Requires ROLE_user.
 *   GET /auth/test/role-political-competitor       — Requires ROLE_political-competitor.
 *   GET /auth/test/role-professional-competitor    — Requires ROLE_professional-competitor.
 */
@RestController
@RequestMapping("/auth/test")
public class OidcTestController {

    // ==================== No auth required ====================

    /** Base endpoint — confirms the controller is registered. */
    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("OIDC Test Controller is active. "
            + "Try /auth/test/inspect (no auth) or /auth/test/secure (requires JWT).");
    }

    /** Always accessible. Verifies the backend is reachable. */
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Map<String, Object>>> publicEndpoint() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("endpoint", "/auth/test/public");
        data.put("authRequired", false);
        data.put("message", "This endpoint is public. No authentication needed.");
        data.put("timestamp", new Date().toString());
        return ResponseEntity.ok(new ApiResponse<>(true, "Public endpoint reached successfully", data));
    }

    /**
     * Diagnostic endpoint — shows the raw auth state.
     * Call without a token to see AnonymousAuthenticationToken.
     * Call with a valid Bearer token to see decoded JWT claims.
     * Call with an invalid token — you'll get 401 before reaching this method.
     *
     * @param jwt the JWT principal, or null if no token was sent
     */
    @GetMapping("/inspect")
    public ResponseEntity<ApiResponse<Map<String, Object>>> inspectAuth(
            @AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("endpoint", "/auth/test/inspect");
        data.put("timestamp", new Date().toString());

        if (jwt == null) {
            data.put("authenticated", false);
            data.put("diagnosis", "No JWT present. Either no Bearer token was sent, "
                + "or the token was invalid and Spring Security stripped it. "
                + "If you sent a token and see this, check that oauth2ResourceServer "
                + "is enabled in SecurityConfiguration.");
            return ResponseEntity.ok(new ApiResponse<>(true, "No JWT token present", data));
        }

        data.put("authenticated", true);
        data.put("subject", jwt.getSubject());
        data.put("issuer", jwt.getIssuer() != null ? jwt.getIssuer().toString() : null);
        data.put("issuedAt", jwt.getIssuedAt() != null ? jwt.getIssuedAt().toString() : null);
        data.put("expiresAt", jwt.getExpiresAt() != null ? jwt.getExpiresAt().toString() : null);
        data.put("preferredUsername", jwt.getClaimAsString("preferred_username"));
        data.put("email", jwt.getClaimAsString("email"));
        data.put("realmRoles", extractRealmRoles(jwt));
        data.put("clientRoles", extractClientRoles(jwt));
        data.put("allClaimKeys", new ArrayList<>(jwt.getClaims().keySet()));
        data.put("diagnosis", "JWT is valid. Token was decoded and validated by the oauth2ResourceServer.");

        return ResponseEntity.ok(new ApiResponse<>(true, "JWT token inspected successfully", data));
    }

    // ==================== Requires authentication ====================

    /** Requires any valid JWT. Returns 401 if not authenticated. */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/secure")
    public ResponseEntity<ApiResponse<Map<String, Object>>> secureEndpoint(
            @AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> data = buildJwtResponse(jwt, "/auth/test/secure");
        return ResponseEntity.ok(new ApiResponse<>(true,
            "You are authenticated! Your token is valid.", data));
    }

    // ==================== Role-based endpoints ====================

    /** Requires ROLE_admin (mapped from Keycloak realm_access.roles). */
    @PreAuthorize("hasRole('admin')")
    @GetMapping("/role-admin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> adminEndpoint(
            @AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> data = buildJwtResponse(jwt, "/auth/test/role-admin");
        data.put("requiredRole", "admin");
        return ResponseEntity.ok(new ApiResponse<>(true, "You have admin access!", data));
    }

    /** Requires ROLE_moderator (mapped from Keycloak realm_access.roles). */
    @PreAuthorize("hasRole('moderator')")
    @GetMapping("/role-moderator")
    public ResponseEntity<ApiResponse<Map<String, Object>>> moderatorEndpoint(
            @AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> data = buildJwtResponse(jwt, "/auth/test/role-moderator");
        data.put("requiredRole", "moderator");
        return ResponseEntity.ok(new ApiResponse<>(true, "You have moderator access!", data));
    }

    /** Requires ROLE_user (mapped from Keycloak realm_access.roles). */
    @PreAuthorize("hasRole('user')")
    @GetMapping("/role-user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> userEndpoint(
            @AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> data = buildJwtResponse(jwt, "/auth/test/role-user");
        data.put("requiredRole", "user");
        return ResponseEntity.ok(new ApiResponse<>(true, "You have user access!", data));
    }

    /** Requires ROLE_political-competitor (assigned when user joins a political group). */
    @PreAuthorize("hasRole('political-competitor')")
    @GetMapping("/role-political-competitor")
    public ResponseEntity<ApiResponse<Map<String, Object>>> politicalCompetitorEndpoint(
            @AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> data = buildJwtResponse(jwt, "/auth/test/role-political-competitor");
        data.put("requiredRole", "political-competitor");
        return ResponseEntity.ok(new ApiResponse<>(true, "You have political-competitor access!", data));
    }

    /** Requires ROLE_professional-competitor (assigned when user joins a professional group). */
    @PreAuthorize("hasRole('professional-competitor')")
    @GetMapping("/role-professional-competitor")
    public ResponseEntity<ApiResponse<Map<String, Object>>> professionalCompetitorEndpoint(
            @AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> data = buildJwtResponse(jwt, "/auth/test/role-professional-competitor");
        data.put("requiredRole", "professional-competitor");
        return ResponseEntity.ok(new ApiResponse<>(true, "You have professional-competitor access!", data));
    }

    // ==================== Helpers ====================

    private Map<String, Object> buildJwtResponse(Jwt jwt, String endpoint) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("endpoint", endpoint);
        data.put("timestamp", new Date().toString());
        data.put("userId", jwt.getSubject());
        data.put("username", jwt.getClaimAsString("preferred_username"));
        data.put("realmRoles", extractRealmRoles(jwt));
        return data;
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof List) {
            return (List<String>) realmAccess.get("roles");
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractClientRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null) return Collections.emptyMap();

        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> clientAccess = (Map<String, Object>) entry.getValue();
                result.put(entry.getKey(), clientAccess.get("roles"));
            }
        }
        return result;
    }
}
