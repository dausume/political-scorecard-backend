package com.asc.politicalscorecard.services.groups;

import com.asc.politicalscorecard.services.auth.KeycloakAdminService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GroupMembershipService {

    private final KeycloakAdminService keycloakAdminService;

    // Group type to realm role mapping
    private static final Map<String, String> GROUP_TYPE_TO_ROLE = Map.of(
            "Political-Group", "political-competitor",
            "Professional-Group", "professional-competitor"
    );

    // Only political groups are limited to one per user
    private static final java.util.Set<String> EXCLUSIVE_GROUP_TYPES = java.util.Set.of("Political-Group");

    public GroupMembershipService(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
        System.out.println("Initializing GroupMembership Service");
    }

    /**
     * Join a group. For political groups, only one is allowed — the user is removed
     * from any existing political group first. For professional groups, users may
     * join as many as they like.
     *
     * @param userId    the Keycloak user ID (from JWT subject)
     * @param groupName the display name of the group in Keycloak
     * @param groupType "Political-Group" or "Professional-Group"
     * @return the user's updated list of realm role names
     */
    public List<String> joinGroup(String userId, String groupName, String groupType) {
        String roleName = GROUP_TYPE_TO_ROLE.get(groupType);
        if (roleName == null) {
            throw new IllegalArgumentException("Unknown group type: " + groupType);
        }

        // Look up the target Keycloak group
        String newGroupId = keycloakAdminService.getGroupIdByName(groupName);

        // For exclusive group types (political), enforce one-group-per-type
        if (EXCLUSIVE_GROUP_TYPES.contains(groupType)) {
            List<Map<String, Object>> currentGroups = keycloakAdminService.getUserGroups(userId);
            if (currentGroups != null) {
                for (Map<String, Object> existingGroup : currentGroups) {
                    String existingType = extractPscType(existingGroup);
                    if (groupType.equals(existingType)) {
                        String existingId = (String) existingGroup.get("id");
                        if (existingId.equals(newGroupId)) {
                            return getCurrentRoleNames(userId);
                        }
                        keycloakAdminService.removeUserFromGroup(userId, existingId);
                    }
                }
            }
        }

        // Add user to the new group
        if (newGroupId != null) {
            keycloakAdminService.addUserToGroup(userId, newGroupId);
        }

        // Assign the realm role (idempotent — Keycloak ignores if already assigned)
        keycloakAdminService.assignRealmRole(userId, roleName);

        return getCurrentRoleNames(userId);
    }

    /**
     * Extract the psc-type attribute from a group representation.
     * Keycloak stores attributes as {"key": ["value"]}.
     */
    @SuppressWarnings("unchecked")
    private String extractPscType(Map<String, Object> group) {
        Object attrs = group.get("attributes");
        if (attrs instanceof Map) {
            Object pscType = ((Map<String, Object>) attrs).get("psc-type");
            if (pscType instanceof List) {
                List<String> values = (List<String>) pscType;
                if (!values.isEmpty()) {
                    return values.get(0);
                }
            }
        }
        return null;
    }

    /**
     * Returns the names of groups the user currently belongs to.
     */
    public List<String> getUserGroupNames(String userId) {
        List<Map<String, Object>> groups = keycloakAdminService.getUserGroups(userId);
        if (groups == null) {
            return List.of();
        }
        return groups.stream()
                .map(g -> (String) g.get("name"))
                .collect(Collectors.toList());
    }

    private List<String> getCurrentRoleNames(String userId) {
        List<Map<String, Object>> roles = keycloakAdminService.getUserRealmRoles(userId);
        return roles.stream()
                .map(r -> (String) r.get("name"))
                .collect(Collectors.toList());
    }
}
