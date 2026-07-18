package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.PolariInstanceDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.PolariInstanceDTO;
import com.asc.politicalscorecard.services.scoringservices.PolariAuthorityService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authenticated proxy for Polari court cases (ncg-2): browsers never
 * write to Polari directly, so case creation and fork-by-fork
 * advancement go through here with the citizen's own bearer token
 * forwarded (same trust posture as AuthorityController's proxies).
 * Reads are public. The Polari instance comes from the registry
 * ('polari-default' unless ?instance= names another registered row).
 */
@RestController
@RequestMapping("/api/court-cases")
public class PolariCourtCaseController {

    private final PolariAuthorityService polariAuthorityService;
    private final PolariInstanceDAO polariInstanceDAO;

    public PolariCourtCaseController(PolariAuthorityService polariAuthorityService,
                                     PolariInstanceDAO polariInstanceDAO) {
        this.polariAuthorityService = polariAuthorityService;
        this.polariInstanceDAO = polariInstanceDAO;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> createCase(
            @RequestParam(required = false) String instance,
            @RequestBody Map<String, Object> body,
            JwtAuthenticationToken authentication) {
        String baseUrl = resolveBaseUrl(instance);
        if (baseUrl == null) {
            return unknownInstance(instance);
        }
        Map<String, Object> result = polariAuthorityService.post(
                baseUrl, "/api/scoring/court-cases/create", body,
                authentication.getToken().getTokenValue());
        return new ResponseEntity<>(result,
                Boolean.TRUE.equals(result.get("ok")) ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{name}/advance")
    public ResponseEntity<Map<String, Object>> advanceCase(
            @PathVariable String name,
            @RequestParam(required = false) String instance,
            @RequestBody Map<String, Object> body,
            JwtAuthenticationToken authentication) {
        String baseUrl = resolveBaseUrl(instance);
        if (baseUrl == null) {
            return unknownInstance(instance);
        }
        Map<String, Object> result = polariAuthorityService.post(
                baseUrl, "/api/scoring/court-cases/" + name + "/advance", body,
                authentication.getToken().getTokenValue());
        return new ResponseEntity<>(result,
                Boolean.TRUE.equals(result.get("ok")) ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Map<String, Object>> caseReport(
            @PathVariable String name,
            @RequestParam(required = false) String instance) {
        String baseUrl = resolveBaseUrl(instance);
        if (baseUrl == null) {
            return unknownInstance(instance);
        }
        Map<String, Object> result = polariAuthorityService.get(
                baseUrl, "/api/scoring/court-cases/" + name, "");
        return new ResponseEntity<>(result,
                Boolean.TRUE.equals(result.get("ok")) ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    private String resolveBaseUrl(String instance) {
        String name = (instance == null || instance.isEmpty()) ? "polari-default" : instance;
        ApiResponse<PolariInstanceDTO> registered = polariInstanceDAO.readByName(name);
        return registered.isSuccess() ? registered.getData().getBaseUrl() : null;
    }

    private ResponseEntity<Map<String, Object>> unknownInstance(String instance) {
        return new ResponseEntity<>(Map.of("ok", false,
                "error", "No registered Polari instance named '"
                        + (instance == null ? "polari-default" : instance) + "'"),
                HttpStatus.NOT_FOUND);
    }
}
