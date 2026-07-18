package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.objects.auth.UserInfo;
import com.asc.politicalscorecard.services.auth.UserInfoService;
import com.asc.politicalscorecard.services.scoringservices.PolariSyncService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CREATE surface for mechanism C's governance objects — the pieces
 * the revamp plan flagged as read-only-with-no-way-to-create:
 * LogicForkVote topics, LogicForkBallot castings, and
 * DecisionProcedureEdge rows. All writes go to Polari through the
 * verified CRUDE path (PolariSyncService), authenticated here; voter
 * identity is token-derived and overwrites anything client-supplied.
 * Tallying/applying stays on Polari's existing endpoints — this
 * controller only creates rows.
 */
@RestController
@RequestMapping("/api/governance")
public class PolariGovernanceController {

    private final PolariSyncService polariSyncService;
    private final UserInfoService userInfoService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PolariGovernanceController(PolariSyncService polariSyncService,
                                      UserInfoService userInfoService) {
        this.polariSyncService = polariSyncService;
        this.userInfoService = userInfoService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logic-fork-votes")
    public ResponseEntity<Map<String, Object>> createLogicForkVote(
            @RequestBody Map<String, Object> body,
            JwtAuthenticationToken authentication) {
        String name = str(body, "name");
        String forkName = str(body, "fork_name");
        Object candidates = body.get("candidate_criterion_names");
        if (name.isEmpty() || forkName.isEmpty() || !(candidates instanceof List)
                || ((List<?>) candidates).isEmpty()) {
            return bad("name, fork_name and a non-empty candidate_criterion_names list are required");
        }
        UserInfo userInfo = userInfoService.extractUserInfo(authentication);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            params.put("display_name", str(body, "display_name"));
            params.put("description", str(body, "description"));
            params.put("decision_procedure_name", str(body, "decision_procedure_name"));
            params.put("fork_name", forkName);
            params.put("candidate_criterion_names_json", objectMapper.writeValueAsString(candidates));
            params.put("mode", body.getOrDefault("mode", "approval"));
            params.put("status", "open");
            params.put("opens_date", str(body, "opens_date"));
            params.put("closes_date", str(body, "closes_date"));
            params.put("provenance_id", "PSC governance UI, created by " + userInfo.getUsername());
            params.put("notes", str(body, "notes"));
            polariSyncService.createPolariRows("LogicForkVote", List.of(params));
            return ok(Map.of("ok", true, "name", name));
        } catch (Exception e) {
            return refused("Polari refused the LogicForkVote: " + e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logic-fork-votes/{voteName}/ballots")
    public ResponseEntity<Map<String, Object>> castLogicForkBallot(
            @PathVariable String voteName,
            @RequestBody Map<String, Object> body,
            JwtAuthenticationToken authentication) {
        UserInfo userInfo = userInfoService.extractUserInfo(authentication);
        try {
            Map<String, Object> params = new HashMap<>();
            // One ballot per voter per vote — deterministic name makes
            // re-casting an idempotent overwrite attempt, not a dup.
            params.put("name", "psc-lfb--" + voteName + "--" + userInfo.getId());
            params.put("vote_name", voteName);
            params.put("voter", userInfo.getUsername());
            params.put("approvals_json", objectMapper.writeValueAsString(
                    body.getOrDefault("approvals", List.of())));
            params.put("sole_choice", str(body, "sole_choice"));
            params.put("ranking_json", objectMapper.writeValueAsString(
                    body.getOrDefault("ranking", List.of())));
            params.put("cast_date", Instant.now().toString());
            params.put("notes", str(body, "notes"));
            polariSyncService.createPolariRows("LogicForkBallot", List.of(params));
            return ok(Map.of("ok", true, "voteName", voteName, "voter", userInfo.getUsername()));
        } catch (Exception e) {
            return refused("Polari refused the ballot: " + e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/decision-procedure-edges")
    public ResponseEntity<Map<String, Object>> createProcedureEdge(
            @RequestBody Map<String, Object> body,
            JwtAuthenticationToken authentication) {
        String name = str(body, "name");
        String procedure = str(body, "decision_procedure_name");
        String toFork = str(body, "to_fork");
        String toTerminal = str(body, "to_terminal");
        if (name.isEmpty() || procedure.isEmpty() || (toFork.isEmpty() && toTerminal.isEmpty())) {
            return bad("name, decision_procedure_name and one of to_fork/to_terminal are required");
        }
        UserInfo userInfo = userInfoService.extractUserInfo(authentication);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            params.put("decision_procedure_name", procedure);
            params.put("from_fork", str(body, "from_fork"));
            params.put("from_outcome", str(body, "from_outcome"));
            params.put("to_fork", toFork);
            params.put("to_terminal", toTerminal);
            params.put("description", str(body, "description")
                    + " [created via PSC governance UI by " + userInfo.getUsername() + "]");
            polariSyncService.createPolariRows("DecisionProcedureEdge", List.of(params));
            return ok(Map.of("ok", true, "name", name));
        } catch (Exception e) {
            return refused("Polari refused the DecisionProcedureEdge: " + e.getMessage());
        }
    }

    private String str(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value != null ? value.toString() : "";
    }

    private ResponseEntity<Map<String, Object>> ok(Map<String, Object> body) {
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> bad(String error) {
        return new ResponseEntity<>(Map.of("ok", false, "error", error), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<String, Object>> refused(String error) {
        return new ResponseEntity<>(Map.of("ok", false, "error", error), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
