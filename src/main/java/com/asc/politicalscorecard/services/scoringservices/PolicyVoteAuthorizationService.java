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

    /** The staff-group prefix staffGroupName() builds with. */
    private static final String STAFF_GROUP_PREFIX = "policy-vote-staff-";

    /**
     * Browse every existing staff authorization: each per-politician
     * staff group with its current members.
     */
    public ApiResponse<java.util.List<java.util.Map<String, Object>>> listAuthorizations() {
        try {
            java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
            for (java.util.Map<String, Object> group : keycloakAdminService.listGroups()) {
                String name = (String) group.get("name");
                if (name == null || !name.startsWith(STAFF_GROUP_PREFIX)) {
                    continue;
                }
                String groupId = (String) group.get("id");
                java.util.List<java.util.Map<String, Object>> members =
                        keycloakAdminService.getGroupMembers(groupId).stream()
                                .map(member -> java.util.Map.<String, Object>of(
                                        "id", member.get("id"),
                                        "username", member.getOrDefault("username", "")))
                                .collect(java.util.stream.Collectors.toList());
                result.add(java.util.Map.of(
                        "politicianName", name.substring(STAFF_GROUP_PREFIX.length()),
                        "groupId", groupId,
                        "members", members));
            }
            return new ApiResponse<>(true, "Staff authorizations retrieved", result);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to list authorizations: " + e.getMessage(), null);
        }
    }

    /**
     * Revoke a staff authorization: remove the user from the
     * politician's staff group.
     */
    public ApiResponse<StaffAuthorizationDTO> revokeStaff(StaffAuthorizationDTO dto) {
        if (dto.getPoliticianName() == null || dto.getPoliticianName().isBlank()
                || dto.getUsername() == null || dto.getUsername().isBlank()) {
            return new ApiResponse<>(false, "politicianName and username are required.", dto);
        }
        try {
            String groupId = keycloakAdminService.getGroupIdByName(
                    PolicyVoteSubmissionService.staffGroupName(dto.getPoliticianName()));
            if (groupId == null) {
                return new ApiResponse<>(false, "No staff group exists for '"
                        + dto.getPoliticianName() + "'.", dto);
            }
            String userId = keycloakAdminService.findUserIdByUsername(dto.getUsername());
            if (userId == null) {
                return new ApiResponse<>(false, "No Keycloak user found with username '"
                        + dto.getUsername() + "'.", dto);
            }
            keycloakAdminService.removeUserFromGroup(userId, groupId);
            return new ApiResponse<>(true, "'" + dto.getUsername()
                    + "' is no longer authorized for '" + dto.getPoliticianName() + "'.", dto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Keycloak refused the revocation: " + e.getMessage(), dto);
        }
    }

    /** Every holder of the policy-voting-admin realm role. */
    public ApiResponse<java.util.List<java.util.Map<String, Object>>> listAdmins() {
        try {
            java.util.List<java.util.Map<String, Object>> admins =
                    keycloakAdminService.getRealmRoleHolders("policy-voting-admin").stream()
                            .map(user -> java.util.Map.<String, Object>of(
                                    "id", user.get("id"),
                                    "username", user.getOrDefault("username", "")))
                            .collect(java.util.stream.Collectors.toList());
            return new ApiResponse<>(true, "Admin role holders retrieved", admins);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to list admins: " + e.getMessage(), null);
        }
    }
}
