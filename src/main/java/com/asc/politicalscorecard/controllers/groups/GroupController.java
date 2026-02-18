package com.asc.politicalscorecard.controllers.groups;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.controllers.responses.ResponseHandler;
import com.asc.politicalscorecard.services.groups.GroupMembershipService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupMembershipService groupMembershipService;

    public GroupController(GroupMembershipService groupMembershipService) {
        this.groupMembershipService = groupMembershipService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<String>>> getMyGroups(JwtAuthenticationToken authentication) {
        String userId = authentication.getToken().getSubject();
        try {
            List<String> groupNames = groupMembershipService.getUserGroupNames(userId);
            ApiResponse<List<String>> response = ResponseHandler.generateSuccessResponse(
                    "User groups retrieved", groupNames);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<List<String>> response = new ApiResponse<>(
                    false, "Failed to retrieve groups: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<List<String>>> joinGroup(
            @RequestBody Map<String, String> body,
            JwtAuthenticationToken authentication) {

        String userId = authentication.getToken().getSubject();
        String groupName = body.get("groupName");
        String groupType = body.get("groupType");

        if (groupName == null || groupType == null) {
            ApiResponse<List<String>> response = new ApiResponse<>(
                    false, "groupName and groupType are required", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            List<String> updatedRoles = groupMembershipService.joinGroup(userId, groupName, groupType);
            ApiResponse<List<String>> response = ResponseHandler.generateSuccessResponse(
                    "Successfully joined group", updatedRoles);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ApiResponse<List<String>> response = new ApiResponse<>(
                    false, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ApiResponse<List<String>> response = new ApiResponse<>(
                    false, "Failed to join group: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
