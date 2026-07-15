package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.PolicyVoteSubmissionDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.StaffAuthorizationDTO;
import com.asc.politicalscorecard.objects.auth.UserInfo;
import com.asc.politicalscorecard.services.auth.UserInfoService;
import com.asc.politicalscorecard.services.scoringservices.PolicyVoteAuthorizationService;
import com.asc.politicalscorecard.services.scoringservices.PolicyVoteSubmissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

/**
 * PolicyVote dual-path submission (2026-07-14 — see
 * PolicyVoteSubmissionService's doc for the full design). Base path:
 * /api/policy-votes
 */
@RestController
@RequestMapping("/api/policy-votes")
public class PolicyVoteSubmissionController {

    private static final String ADMIN_AUTHORITY = "ROLE_policy-voting-admin";

    private final PolicyVoteSubmissionService submissionService;
    private final PolicyVoteAuthorizationService authorizationService;
    private final UserInfoService userInfoService;

    @Autowired
    public PolicyVoteSubmissionController(
            PolicyVoteSubmissionService submissionService,
            PolicyVoteAuthorizationService authorizationService,
            UserInfoService userInfoService) {
        this.submissionService = submissionService;
        this.authorizationService = authorizationService;
        this.userInfoService = userInfoService;
    }

    /**
     * Submit a PolicyVote record. Requires authentication; whether the
     * caller is treated as the admin path or the staff path is
     * resolved here from their real granted authorities/group
     * membership — never a client-supplied flag.
     * POST /api/policy-votes/submit
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<PolicyVoteSubmissionDTO>> submit(
            @RequestBody PolicyVoteSubmissionDTO dto,
            Authentication authentication) {
        UserInfo userInfo = userInfoService.extractUserInfo(authentication);
        if (userInfo == null || userInfo.getId() == null) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Could not resolve an authenticated user id.", dto),
                    HttpStatus.UNAUTHORIZED);
        }
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ADMIN_AUTHORITY::equals);

        ApiResponse<PolicyVoteSubmissionDTO> response =
                submissionService.submit(dto, userInfo.getId(), isAdmin);
        return response.isSuccess()
                ? new ResponseEntity<>(response, HttpStatus.CREATED)
                : new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Authorize a Keycloak user as a politician's PolicyVote submitter
     * — admin-only.
     * POST /api/policy-votes/authorize-staff
     */
    @PreAuthorize("hasRole('policy-voting-admin')")
    @PostMapping("/authorize-staff")
    public ResponseEntity<ApiResponse<StaffAuthorizationDTO>> authorizeStaff(
            @RequestBody StaffAuthorizationDTO dto) {
        ApiResponse<StaffAuthorizationDTO> response = authorizationService.authorizeStaff(dto);
        return response.isSuccess()
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
