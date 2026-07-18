package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.controllers.responses.ResponseHandler;
import com.asc.politicalscorecard.databases.daos.scoringdaos.PolariInstanceDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.GroupInstanceBindingDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.PolariInstanceDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.TermProvenanceDTO;
import com.asc.politicalscorecard.objects.auth.UserInfo;
import com.asc.politicalscorecard.services.auth.UserInfoService;
import com.asc.politicalscorecard.services.scoringservices.GroupAuthorityService;
import com.asc.politicalscorecard.services.scoringservices.PolariAuthorityService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Group ↔ Polari-instance authority surface.
 *
 * Reads are public (registry, bindings, provenance are civic data).
 * Mutations require authentication; the citizen's own bearer token is
 * FORWARDED to Polari so both Keycloak assessments happen — PSC's
 * (@PreAuthorize here) and Polari's (its middleware re-verifies the
 * same token). Authority semantics (primary/shared grants, the
 * three-check verdict) are enforced Polari-side; PSC enforces its own
 * side of the binding and records provenance.
 *
 * The /polari/{instance}/... endpoints are thin authenticated proxies
 * so browsers keep never writing to Polari directly.
 */
@RestController
@RequestMapping("/api/authority")
public class AuthorityController {

    private final GroupAuthorityService groupAuthorityService;
    private final PolariAuthorityService polariAuthorityService;
    private final PolariInstanceDAO polariInstanceDAO;
    private final UserInfoService userInfoService;

    public AuthorityController(GroupAuthorityService groupAuthorityService,
                               PolariAuthorityService polariAuthorityService,
                               PolariInstanceDAO polariInstanceDAO,
                               UserInfoService userInfoService) {
        this.groupAuthorityService = groupAuthorityService;
        this.polariAuthorityService = polariAuthorityService;
        this.polariInstanceDAO = polariInstanceDAO;
        this.userInfoService = userInfoService;
    }

    // ------------------------------------------------------------ //
    // Instance registry
    // ------------------------------------------------------------ //

    @GetMapping("/instances")
    public ResponseEntity<ApiResponse<List<PolariInstanceDTO>>> listInstances() {
        ApiResponse<List<PolariInstanceDTO>> response = groupAuthorityService.listInstances();
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasRole('policy-voting-admin')")
    @PostMapping("/instances")
    public ResponseEntity<ApiResponse<PolariInstanceDTO>> registerInstance(
            @RequestBody PolariInstanceDTO dto) {
        if (dto.getName() == null || dto.getName().isEmpty()
                || dto.getBaseUrl() == null || dto.getBaseUrl().isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(false, "name and baseUrl are required", dto),
                    HttpStatus.BAD_REQUEST);
        }
        ApiResponse<PolariInstanceDTO> response = groupAuthorityService.registerInstance(dto);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    // ------------------------------------------------------------ //
    // Bindings (the PSC side of "defined on both sides")
    // ------------------------------------------------------------ //

    @GetMapping("/bindings")
    public ResponseEntity<ApiResponse<List<GroupInstanceBindingDTO>>> listBindings() {
        ApiResponse<List<GroupInstanceBindingDTO>> response = groupAuthorityService.listBindings();
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/bindings")
    public ResponseEntity<Map<String, Object>> createBinding(
            @RequestBody Map<String, String> body,
            JwtAuthenticationToken authentication) {
        String groupName = body.get("groupName");
        String instanceName = body.get("instanceName");
        if (groupName == null || instanceName == null) {
            return new ResponseEntity<>(Map.of("ok", false,
                    "error", "groupName and instanceName are required"), HttpStatus.BAD_REQUEST);
        }
        UserInfo userInfo = userInfoService.extractUserInfo(authentication);
        Map<String, Object> result = groupAuthorityService.createBinding(
                groupName, body.getOrDefault("groupType", ""), instanceName,
                userInfo, authentication.getToken().getTokenValue());
        return new ResponseEntity<>(result,
                Boolean.TRUE.equals(result.get("ok")) ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // ------------------------------------------------------------ //
    // Term-availability signals + provenance
    // ------------------------------------------------------------ //

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/signals/term-availability")
    public ResponseEntity<Map<String, Object>> submitTermAvailability(
            @RequestBody Map<String, String> body,
            JwtAuthenticationToken authentication) {
        String termName = body.get("termName");
        String contextName = body.get("contextName");
        String groupName = body.get("groupName");
        String instanceName = body.get("instanceName");
        if (termName == null || contextName == null || groupName == null || instanceName == null) {
            return new ResponseEntity<>(Map.of("ok", false,
                    "error", "termName, contextName, groupName and instanceName are required"),
                    HttpStatus.BAD_REQUEST);
        }
        UserInfo userInfo = userInfoService.extractUserInfo(authentication);
        Map<String, Object> result = groupAuthorityService.submitTermAvailability(
                termName, contextName, groupName, instanceName,
                body.getOrDefault("conceptName", ""),
                body.getOrDefault("description", ""),
                body.getOrDefault("category", ""),
                userInfo, authentication.getToken().getTokenValue());
        return new ResponseEntity<>(result,
                Boolean.TRUE.equals(result.get("ok")) ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping("/provenance")
    public ResponseEntity<ApiResponse<List<TermProvenanceDTO>>> listProvenance(
            @RequestParam(required = false) String termId) {
        ApiResponse<List<TermProvenanceDTO>> response = groupAuthorityService.listProvenance(termId);
        return new ResponseEntity<>(response, response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ------------------------------------------------------------ //
    // Authenticated proxies to Polari's authority endpoints
    // ------------------------------------------------------------ //

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/polari/{instanceName}/groups/grant")
    public ResponseEntity<Map<String, Object>> polariGroupGrant(
            @PathVariable String instanceName,
            @RequestBody Map<String, Object> body,
            JwtAuthenticationToken authentication) {
        return proxyPost(instanceName, "/api/scoring/authority/groups/grant", body, authentication);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/polari/{instanceName}/groups/revoke")
    public ResponseEntity<Map<String, Object>> polariGroupRevoke(
            @PathVariable String instanceName,
            @RequestBody Map<String, Object> body,
            JwtAuthenticationToken authentication) {
        return proxyPost(instanceName, "/api/scoring/authority/groups/revoke", body, authentication);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/polari/{instanceName}/instances/grant")
    public ResponseEntity<Map<String, Object>> polariInstanceGrant(
            @PathVariable String instanceName,
            @RequestBody Map<String, Object> body,
            JwtAuthenticationToken authentication) {
        return proxyPost(instanceName, "/api/scoring/authority/instances/grant", body, authentication);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/polari/{instanceName}/instances/revoke")
    public ResponseEntity<Map<String, Object>> polariInstanceRevoke(
            @PathVariable String instanceName,
            @RequestBody Map<String, Object> body,
            JwtAuthenticationToken authentication) {
        return proxyPost(instanceName, "/api/scoring/authority/instances/revoke", body, authentication);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/polari/{instanceName}/bindings/propose")
    public ResponseEntity<Map<String, Object>> polariBindingPropose(
            @PathVariable String instanceName,
            @RequestBody Map<String, Object> body,
            JwtAuthenticationToken authentication) {
        return proxyPost(instanceName, "/api/scoring/authority/bindings/propose", body, authentication);
    }

    @GetMapping("/polari/{instanceName}/report")
    public ResponseEntity<Map<String, Object>> polariReport(
            @PathVariable String instanceName,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String instance,
            @RequestParam(required = false) String subject,
            JwtAuthenticationToken authentication) {
        ApiResponse<PolariInstanceDTO> registered = polariInstanceDAO.readByName(instanceName);
        if (!registered.isSuccess()) {
            return new ResponseEntity<>(Map.of("ok", false,
                    "error", registered.getMessage()), HttpStatus.NOT_FOUND);
        }
        String bearer = authentication != null ? authentication.getToken().getTokenValue() : "";
        Map<String, Object> result = polariAuthorityService.authorityReport(
                registered.getData().getBaseUrl(), group, instance, subject, bearer);
        return new ResponseEntity<>(result,
                Boolean.TRUE.equals(result.get("ok")) ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ResponseEntity<Map<String, Object>> proxyPost(String instanceName, String path,
                                                          Map<String, Object> body,
                                                          JwtAuthenticationToken authentication) {
        ApiResponse<PolariInstanceDTO> registered = polariInstanceDAO.readByName(instanceName);
        if (!registered.isSuccess()) {
            return new ResponseEntity<>(Map.of("ok", false,
                    "error", registered.getMessage()), HttpStatus.NOT_FOUND);
        }
        Map<String, Object> result = polariAuthorityService.post(
                registered.getData().getBaseUrl(), path, body,
                authentication.getToken().getTokenValue());
        return new ResponseEntity<>(result,
                Boolean.TRUE.equals(result.get("ok")) ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
