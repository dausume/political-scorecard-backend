package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.ScoreAssertionSubmissionDTO;
import com.asc.politicalscorecard.objects.auth.UserInfo;
import com.asc.politicalscorecard.services.auth.UserInfoService;
import com.asc.politicalscorecard.services.scoringservices.ScoreAssertionSubmissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Citizen-facing ScoreAssertion submission (2026-07-14 general-scoring
 * follow-up — see ScoreAssertionSubmissionService's doc for why this
 * routes through PSC's own authenticated backend rather than writing
 * to Polari's generic CRUDE endpoint directly, and why the equivalent
 * PolicyVote-creation feature was deliberately NOT built here).
 * Base path: /api/score-assertions
 */
@RestController
@RequestMapping("/api/score-assertions")
public class ScoreAssertionController {

    private final ScoreAssertionSubmissionService submissionService;
    private final UserInfoService userInfoService;

    @Autowired
    public ScoreAssertionController(ScoreAssertionSubmissionService submissionService,
                                    UserInfoService userInfoService) {
        this.submissionService = submissionService;
        this.userInfoService = userInfoService;
    }

    /**
     * Submit a new ScoreAssertion claim about a policy. Requires
     * authentication — asserted_by is resolved server-side from the
     * Keycloak JWT (UserInfoService.extractUserInfo), never
     * client-supplied, same pattern as PolariVoteController.castVote.
     * Always lands at Polari status='asserted' (never pre-confirmed),
     * so it has zero live scoring effect until a human reviews it.
     * POST /api/score-assertions/submit
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ScoreAssertionSubmissionDTO>> submit(
            @RequestBody ScoreAssertionSubmissionDTO dto,
            Authentication authentication) {
        UserInfo userInfo = userInfoService.extractUserInfo(authentication);
        if (userInfo == null || userInfo.getId() == null) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Could not resolve an authenticated user id.", dto),
                    HttpStatus.UNAUTHORIZED);
        }
        ApiResponse<ScoreAssertionSubmissionDTO> response = submissionService.submit(dto, userInfo.getId());
        return response.isSuccess()
                ? new ResponseEntity<>(response, HttpStatus.CREATED)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
