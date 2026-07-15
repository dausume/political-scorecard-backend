package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.StaffAuthorizationDTO;
import com.asc.politicalscorecard.services.auth.KeycloakAdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Lets a policy-voting-admin designate a Keycloak user as a specific
 * politician's authorized PolicyVote submitter (2026-07-14 — the
 * "cabinet people or assistants they personally authorized" half of
 * Dustin's two-path design). Adds the target user to a per-politician
 * Keycloak group (created on first use); PolicyVoteSubmissionService
 * checks membership in that same group at submission time.
 *
 * This does NOT itself verify that the real-world relationship is
 * genuine — an admin granting this is vouching for it, same trust
 * model as the role itself (see PolicyVoteSubmissionService's doc).
 */
@Service
public class PolicyVoteAuthorizationService {

    private final KeycloakAdminService keycloakAdminService;

    @Autowired
    public PolicyVoteAuthorizationService(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    public ApiResponse<StaffAuthorizationDTO> authorizeStaff(StaffAuthorizationDTO dto) {
        if (dto.getPoliticianName() == null || dto.getPoliticianName().isBlank()) {
            return new ApiResponse<>(false, "politicianName is required.", dto);
        }
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            return new ApiResponse<>(false, "username is required.", dto);
        }

        String userId = keycloakAdminService.findUserIdByUsername(dto.getUsername());
        if (userId == null) {
            return new ApiResponse<>(false, "No Keycloak user found with username '"
                    + dto.getUsername() + "'.", dto);
        }

        try {
            String groupId = keycloakAdminService.ensureGroupExists(
                    PolicyVoteSubmissionService.staffGroupName(dto.getPoliticianName()));
            keycloakAdminService.addUserToGroup(userId, groupId);
            return new ApiResponse<>(true,
                    "'" + dto.getUsername() + "' is now authorized to submit vote records for '"
                            + dto.getPoliticianName() + "'.", dto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Keycloak refused the authorization: " + e.getMessage(), dto);
        }
    }
}
