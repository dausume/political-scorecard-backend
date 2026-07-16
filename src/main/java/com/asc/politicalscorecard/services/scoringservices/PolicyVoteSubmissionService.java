package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.PolicyVoteSubmissionDTO;
import com.asc.politicalscorecard.services.auth.KeycloakAdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PolicyVote submission (2026-07-14 dual-path follow-up). Per Dustin
 * (verbatim): "PolicyVote should either cite an official government
 * source for the vote record, and be defined by a policy voting
 * admin, or it should be defined by the Politician themselves or one
 * of their cabinet people or assistants they personally authorized to
 * handle it."
 *
 * Two authorization paths, checked here (not by @PreAuthorize alone,
 * since either path is acceptable on the same endpoint):
 *   - ADMIN path: caller holds ROLE_policy-voting-admin (resolved from
 *     the JWT's realm_access.roles by SecurityConfiguration, same as
 *     every other hasRole() check in this app) AND supplies a
 *     non-blank officialSourceUrl citation. No group membership
 *     needed — an admin can record any politician's vote, as long as
 *     it's cited.
 *   - STAFF path: caller is a member of the Keycloak group
 *     "policy-vote-staff-<politician-slug>" (see PolicyVoteAuthorizationService
 *     for how that group gets populated) — the politician's own
 *     account or someone they've personally authorized. No source URL
 *     required — the submitter's own authorized identity IS the
 *     record's provenance.
 *
 * Unlike ScoreAssertionSubmissionService, there is NO further review
 * gate after this — PolicyVote has no status field, so a submission
 * here counts toward the named politician's score immediately. That's
 * why both paths require real, deliberately-granted authorization
 * rather than just "any authenticated citizen."
 */
@Service
public class PolicyVoteSubmissionService {

    private static final Logger logger = Logger.getLogger(PolicyVoteSubmissionService.class.getName());
    private static final List<String> VOTE_KINDS = List.of("yea", "nay", "abstain");

    private final PolariSyncService polariSyncService;
    private final KeycloakAdminService keycloakAdminService;

    @Autowired
    public PolicyVoteSubmissionService(PolariSyncService polariSyncService,
                                       KeycloakAdminService keycloakAdminService) {
        this.polariSyncService = polariSyncService;
        this.keycloakAdminService = keycloakAdminService;
    }

    public static String staffGroupName(String politicianName) {
        return "policy-vote-staff-" + politicianName;
    }

    public ApiResponse<PolicyVoteSubmissionDTO> submit(
            PolicyVoteSubmissionDTO dto, String voterId, boolean isAdmin) {
        if (dto.getPoliticianName() == null || dto.getPoliticianName().isBlank()) {
            return new ApiResponse<>(false, "politicianName is required.", dto);
        }
        if (dto.getPolicyName() == null || dto.getPolicyName().isBlank()) {
            return new ApiResponse<>(false, "policyName is required.", dto);
        }
        String vote = VOTE_KINDS.contains(dto.getVote()) ? dto.getVote() : null;
        if (vote == null) {
            return new ApiResponse<>(false, "vote must be one of " + VOTE_KINDS + ".", dto);
        }

        String authorizedVia;
        String source;

        if (isAdmin) {
            if (dto.getOfficialSourceUrl() == null || dto.getOfficialSourceUrl().isBlank()) {
                return new ApiResponse<>(false,
                        "An official government source citation (officialSourceUrl) is required "
                                + "when submitting as a policy voting admin.", dto);
            }
            authorizedVia = "admin";
            source = dto.getOfficialSourceUrl();
        } else {
            String groupId = keycloakAdminService.getGroupIdByName(staffGroupName(dto.getPoliticianName()));
            boolean isStaff = groupId != null && keycloakAdminService.getUserGroups(voterId).stream()
                    .anyMatch(g -> groupId.equals(g.get("id")));
            if (!isStaff) {
                return new ApiResponse<>(false,
                        "Not authorized to submit votes for '" + dto.getPoliticianName() + "' — "
                                + "you must either hold the policy-voting-admin role (with a cited "
                                + "official source) or be authorized by that politician as their own "
                                + "submitter.", dto);
            }
            authorizedVia = "staff";
            source = dto.getOfficialSourceUrl() != null && !dto.getOfficialSourceUrl().isBlank()
                    ? dto.getOfficialSourceUrl()
                    : "self-attested by an authorized submitter for " + dto.getPoliticianName();
        }

        String name = "psc-policyvote-" + java.util.UUID.randomUUID();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            params.put("politician_name", dto.getPoliticianName());
            params.put("policy_name", dto.getPolicyName());
            params.put("vote", vote);
            params.put("vote_date", dto.getVoteDate() == null ? "" : dto.getVoteDate());
            params.put("chamber", dto.getChamber() == null ? "" : dto.getChamber());
            params.put("session", dto.getSession() == null ? "" : dto.getSession());
            params.put("source", source);
            params.put("provenance_id", "psc-citizen-submission-" + authorizedVia);
            params.put("contributed_by", voterId);
            params.put("notes", dto.getNotes() == null ? "" : dto.getNotes());

            String createdName = polariSyncService.submitPolicyVote(params);
            dto.setVoteName(createdName);
            dto.setAuthorizedVia(authorizedVia);
            return new ApiResponse<>(true, "Vote record submitted.", dto);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error submitting PolicyVote to Polari: ", e);
            return new ApiResponse<>(false, "Polari refused the submission: " + e.getMessage(), dto);
        }
    }
}
