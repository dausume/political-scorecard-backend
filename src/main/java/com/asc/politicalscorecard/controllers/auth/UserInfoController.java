package com.asc.politicalscorecard.controllers.auth;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.controllers.responses.ResponseHandler;
import com.asc.politicalscorecard.objects.auth.UserInfo;
import com.asc.politicalscorecard.services.auth.UserInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @Autowired
    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    // Baseline Test - Public endpoint
    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("Successfully hit the Auth Controller.");
    }

    /**
     * Test endpoint that requires authentication - use this to verify JWT token validation works
     *
     * @param authentication The authentication object injected by Spring Security
     * @return ResponseEntity containing UserInfo with userId, username, and roles
     */
    @GetMapping("/test-auth")
    public ResponseEntity<ApiResponse<UserInfo>> testAuth(Authentication authentication) {
        UserInfo userInfo = userInfoService.extractUserInfo(authentication);

        if (userInfo != null) {
            ApiResponse<UserInfo> response = ResponseHandler.generateSuccessResponse("Authentication successful! User info retrieved from JWT token", userInfo);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<UserInfo> response = new ApiResponse<>(false, "Unable to extract user information from token", null);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Get current authenticated user's information from JWT token
     * This endpoint is public for now, but will return user info if a valid token is provided
     *
     * @param authentication The authentication object injected by Spring Security
     * @return ResponseEntity containing UserInfo with userId, username, and roles
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfo>> getCurrentUser(Authentication authentication) {
        UserInfo userInfo = userInfoService.extractUserInfo(authentication);

        if (userInfo != null) {
            ApiResponse<UserInfo> response = ResponseHandler.generateSuccessResponse("User information retrieved successfully", userInfo);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<UserInfo> response = new ApiResponse<>(false, "Unable to extract user information from token", null);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}
