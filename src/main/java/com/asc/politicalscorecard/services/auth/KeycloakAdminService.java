package com.asc.politicalscorecard.services.auth;

import com.asc.politicalscorecard.config.KeycloakAdminConfig;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakAdminService {

    private final KeycloakAdminConfig config;
    private final RestClient restClient;

    private String cachedToken;
    private Instant tokenExpiry = Instant.MIN;

    public KeycloakAdminService(
            KeycloakAdminConfig config,
            @Qualifier("keycloakAdminRestClient") RestClient restClient) {
        this.config = config;
        this.restClient = restClient;
        System.out.println("Initializing KeycloakAdmin Service");
    }

    /**
     * Obtain a service account token via client_credentials grant.
     * Caches the token and refreshes 30 seconds before expiry.
     */
    public String getServiceAccountToken() {
        if (cachedToken != null && Instant.now().isBefore(tokenExpiry)) {
            return cachedToken;
        }

        String tokenUrl = "/realms/" + config.getRealm() + "/protocol/openid-connect/token";

        String formBody = "grant_type=client_credentials"
                + "&client_id=" + config.getClientId()
                + "&client_secret=" + config.getClientSecret();

        Map<String, Object> response = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formBody)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        if (response == null || !response.containsKey("access_token")) {
            throw new RuntimeException("Failed to obtain service account token from Keycloak");
        }

        cachedToken = (String) response.get("access_token");
        int expiresIn = response.get("expires_in") instanceof Number
                ? ((Number) response.get("expires_in")).intValue()
                : 300;
        tokenExpiry = Instant.now().plusSeconds(expiresIn - 30);

        return cachedToken;
    }

    /**
     * Assign a realm role to a user.
     */
    public void assignRealmRole(String userId, String roleName) {
        String token = getServiceAccountToken();
        String adminBase = "/admin/realms/" + config.getRealm();

        // Get the role representation
        Map<String, Object> role = restClient.get()
                .uri(adminBase + "/roles/" + roleName)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        if (role == null) {
            throw new RuntimeException("Realm role not found: " + roleName);
        }

        // Assign the role to the user
        restClient.post()
                .uri(adminBase + "/users/" + userId + "/role-mappings/realm")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(role))
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Add a user to a Keycloak group by group ID.
     */
    public void addUserToGroup(String userId, String groupId) {
        String token = getServiceAccountToken();
        String adminBase = "/admin/realms/" + config.getRealm();

        restClient.put()
                .uri(adminBase + "/users/" + userId + "/groups/" + groupId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Get a user's current realm role mappings.
     */
    public List<Map<String, Object>> getUserRealmRoles(String userId) {
        String token = getServiceAccountToken();
        String adminBase = "/admin/realms/" + config.getRealm();

        return restClient.get()
                .uri(adminBase + "/users/" + userId + "/role-mappings/realm")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * Get the groups a user belongs to.
     */
    public List<Map<String, Object>> getUserGroups(String userId) {
        String token = getServiceAccountToken();
        String adminBase = "/admin/realms/" + config.getRealm();

        return restClient.get()
                .uri(adminBase + "/users/" + userId + "/groups")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
    }

    /**
     * Remove a user from a Keycloak group by group ID.
     */
    public void removeUserFromGroup(String userId, String groupId) {
        String token = getServiceAccountToken();
        String adminBase = "/admin/realms/" + config.getRealm();

        restClient.delete()
                .uri(adminBase + "/users/" + userId + "/groups/" + groupId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Look up a Keycloak group by name. Returns the group ID or null if not found.
     */
    public String getGroupIdByName(String groupName) {
        String token = getServiceAccountToken();
        String adminBase = "/admin/realms/" + config.getRealm();

        List<Map<String, Object>> groups = restClient.get()
                .uri(adminBase + "/groups?search=" + groupName + "&exact=true")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

        if (groups != null && !groups.isEmpty()) {
            return (String) groups.get(0).get("id");
        }
        return null;
    }

    /**
     * Look up a Keycloak user's id by exact username (2026-07-14
     * PolicyVote dual-path submission — resolving WHO an admin means
     * to authorize as a politician's staff before adding them to that
     * politician's group). Returns null if no exact match exists.
     */
    public String findUserIdByUsername(String username) {
        String token = getServiceAccountToken();
        String adminBase = "/admin/realms/" + config.getRealm();

        List<Map<String, Object>> users = restClient.get()
                .uri(adminBase + "/users?username=" + username + "&exact=true")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

        if (users != null && !users.isEmpty()) {
            return (String) users.get(0).get("id");
        }
        return null;
    }

    /**
     * Returns an existing group's id by name, or creates it first if it
     * doesn't exist yet (2026-07-14 — politician-staff authorization
     * groups are created on first use, one per politician, rather than
     * needing to be pre-seeded).
     */
    public String ensureGroupExists(String groupName) {
        String existing = getGroupIdByName(groupName);
        if (existing != null) {
            return existing;
        }

        String token = getServiceAccountToken();
        String adminBase = "/admin/realms/" + config.getRealm();

        restClient.post()
                .uri(adminBase + "/groups")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("name", groupName))
                .retrieve()
                .toBodilessEntity();

        return getGroupIdByName(groupName);
    }
}
