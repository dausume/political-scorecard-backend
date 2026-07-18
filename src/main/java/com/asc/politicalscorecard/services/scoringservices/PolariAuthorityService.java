package com.asc.politicalscorecard.services.scoringservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PSC-backend bridge to Polari's group/instance-authority endpoints
 * (/api/scoring/authority/*, /api/scoring/signals/*).
 *
 * Unlike PolariSyncService (service-identity writes), every call here
 * FORWARDS THE CITIZEN'S OWN BEARER TOKEN: Polari's Keycloak
 * middleware re-verifies the same principal independently, so "the
 * auth of keycloak assesses yes this person is authoritative" holds
 * on BOTH sides of the trust boundary — PSC never asserts an
 * identity to Polari that Polari cannot check itself. Browsers still
 * never write to Polari directly; PSC remains the only caller.
 *
 * The Polari instance is resolved PER CALL by base URL (from the
 * polari_instance registry), not from the single app.polari.api-url
 * property — this is what makes multiple authoritative instances
 * possible.
 */
@Service
public class PolariAuthorityService {

    private static final Logger logger = Logger.getLogger(PolariAuthorityService.class.getName());
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PolariAuthorityService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /** POST a JSON body to a Polari path, forwarding the bearer. */
    @SuppressWarnings("unchecked")
    public Map<String, Object> post(String baseUrl, String path, Map<String, Object> body, String bearerToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (bearerToken != null && !bearerToken.isEmpty()) {
                headers.setBearerAuth(bearerToken);
            }
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + path, HttpMethod.POST, entity, String.class);
            return objectMapper.readValue(response.getBody(), Map.class);
        } catch (HttpStatusCodeException e) {
            // Polari's refusals (422) carry evidence-bearing bodies —
            // pass them through instead of flattening to an error string.
            return parseErrorBody(e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Polari authority POST " + path + " failed: ", e);
            return errorMap("Polari call failed: " + e.getMessage());
        }
    }

    /** GET a Polari path, forwarding the bearer. */
    @SuppressWarnings("unchecked")
    public Map<String, Object> get(String baseUrl, String path, String bearerToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (bearerToken != null && !bearerToken.isEmpty()) {
                headers.setBearerAuth(bearerToken);
            }
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + path, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            return objectMapper.readValue(response.getBody(), Map.class);
        } catch (HttpStatusCodeException e) {
            return parseErrorBody(e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Polari authority GET " + path + " failed: ", e);
            return errorMap("Polari call failed: " + e.getMessage());
        }
    }

    /**
     * The instance's own identity (POLARI_INSTANCE_NAME over there) —
     * the PSC registry name is a local label and need not match, so
     * binding names are always built from what the instance calls
     * itself.
     */
    public String getSelfInstanceName(String baseUrl, String bearerToken) {
        Map<String, Object> report = get(baseUrl, "/api/scoring/authority/report", bearerToken);
        Object self = report.get("selfInstance");
        return self != null ? self.toString() : "";
    }

    public Map<String, Object> confirmBindingRemote(String baseUrl, String polariBindingName, String bearerToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("confirmed_by", "psc-backend");
        return post(baseUrl, "/api/scoring/authority/bindings/" + polariBindingName + "/confirm-remote",
                body, bearerToken);
    }

    public Map<String, Object> submitTermSignal(String baseUrl, Map<String, Object> signal, String bearerToken) {
        return post(baseUrl, "/api/scoring/signals/term-availability", signal, bearerToken);
    }

    public Map<String, Object> authorityReport(String baseUrl, String group, String instance,
                                               String subject, String bearerToken) {
        StringBuilder path = new StringBuilder("/api/scoring/authority/report?");
        if (group != null && !group.isEmpty()) path.append("group=").append(group).append("&");
        if (instance != null && !instance.isEmpty()) path.append("instance=").append(instance).append("&");
        if (subject != null && !subject.isEmpty()) path.append("subject=").append(subject);
        return get(baseUrl, path.toString(), bearerToken);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseErrorBody(HttpStatusCodeException e) {
        try {
            return objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
        } catch (Exception parseFailure) {
            return errorMap("Polari returned " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
        }
    }

    private Map<String, Object> errorMap(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("ok", false);
        error.put("error", message);
        return error;
    }
}
