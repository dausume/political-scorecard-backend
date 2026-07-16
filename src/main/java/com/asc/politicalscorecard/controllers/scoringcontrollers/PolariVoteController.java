package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.PolariVoteTopicDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.WorldviewVoteDTO;
import com.asc.politicalscorecard.objects.auth.UserInfo;
import com.asc.politicalscorecard.services.auth.UserInfoService;
import com.asc.politicalscorecard.services.scoringservices.PolariVoteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for PSC-hosted public votes over candidate
 * definitions drafted in Polari (2026-07-14 ballot-hosting
 * architecture move — see DEMOCRATIC_SCORECARD_REVAMP_PLAN.md's
 * "Architecture correction" section for the full design).
 * Base path: /api/polari-votes
 */
@RestController
@RequestMapping("/api/polari-votes")
public class PolariVoteController {

    private final PolariVoteService polariVoteService;
    private final UserInfoService userInfoService;

    @Autowired
    public PolariVoteController(PolariVoteService polariVoteService, UserInfoService userInfoService) {
        this.polariVoteService = polariVoteService;
        this.userInfoService = userInfoService;
    }

    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("Successfully hit the PolariVote Controller.");
    }

    /**
     * Create a new vote topic. Not auth-gated today (matches
     * WorldviewElectionController.createElection's existing
     * convention — createdBy is a client-supplied string, same as
     * every other authoring endpoint in this codebase); tightening to
     * an admin/moderator role is a real follow-up, not silently
     * assumed here.
     * POST /api/polari-votes/create
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PolariVoteTopicDTO>> createTopic(@RequestBody PolariVoteTopicDTO topicDTO) {
        ApiResponse<PolariVoteTopicDTO> response = polariVoteService.createTopic(topicDTO);
        return response.isSuccess()
                ? new ResponseEntity<>(response, HttpStatus.CREATED)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PolariVoteTopicDTO>>> getAllTopics() {
        ApiResponse<List<PolariVoteTopicDTO>> response = polariVoteService.getAllTopics();
        return response.isSuccess()
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PolariVoteTopicDTO>> getTopicById(@PathVariable String id) {
        ApiResponse<PolariVoteTopicDTO> response = polariVoteService.getTopicById(id);
        return response.isSuccess()
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Cast a vote. Requires authentication — the voter id resolved
     * here is PSC's own anonymized Keycloak subject claim
     * (UserInfoService.extractUserInfo), never a real name, matching
     * Dustin's "anonymized usernames or ids not real names" for both
     * sides of the PSC/Polari split. Any voterId the client sent in
     * the request body is IGNORED and overwritten — a client can
     * never cast a vote as someone else.
     * POST /api/polari-votes/{id}/ballots
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/ballots")
    public ResponseEntity<ApiResponse<WorldviewVoteDTO>> castVote(
            @PathVariable String id,
            @RequestBody WorldviewVoteDTO voteDTO,
            Authentication authentication) {
        UserInfo userInfo = userInfoService.extractUserInfo(authentication);
        if (userInfo == null || userInfo.getId() == null) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Could not resolve an authenticated voter id.", voteDTO),
                    HttpStatus.UNAUTHORIZED);
        }
        voteDTO.setVoterId(userInfo.getId());
        ApiResponse<WorldviewVoteDTO> response = polariVoteService.castVote(id, voteDTO);
        return response.isSuccess()
                ? new ResponseEntity<>(response, HttpStatus.CREATED)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Raw vote-count preview (NOT the real weighted tally — that only
     * happens in Polari once synced).
     * GET /api/polari-votes/{id}/results
     */
    @GetMapping("/{id}/results")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getResults(@PathVariable String id) {
        ApiResponse<Map<String, Object>> response = polariVoteService.getResults(id);
        return response.isSuccess()
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Close a topic — stops accepting new ballots, required before
     * syncing to Polari.
     * POST /api/polari-votes/{id}/close
     */
    @PostMapping("/{id}/close")
    public ResponseEntity<ApiResponse<PolariVoteTopicDTO>> closeTopic(@PathVariable String id) {
        ApiResponse<PolariVoteTopicDTO> response = polariVoteService.closeTopic(id);
        return response.isSuccess()
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Replay a CLOSED topic's votes into Polari and apply the result
     * — the "derivative goes back to Polari to be resolved" step.
     * POST /api/polari-votes/{id}/sync
     */
    @PostMapping("/{id}/sync")
    public ResponseEntity<ApiResponse<PolariVoteTopicDTO>> syncToPolari(@PathVariable String id) {
        ApiResponse<PolariVoteTopicDTO> response = polariVoteService.syncToPolari(id);
        return response.isSuccess()
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
