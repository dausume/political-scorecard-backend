package com.asc.politicalscorecard.services.auth;

import com.asc.politicalscorecard.objects.auth.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoService {

    public UserInfoService() {
        System.out.println("Initializing UserInfo Service");
    }

    /**
     * Extracts minimal user information from the authenticated JWT token
     *
     * @param authentication The authentication object from the security context
     * @return UserInfo object containing userId, username, and roles from the JWT
     */
    public UserInfo extractUserInfo(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            Jwt jwt = jwtAuth.getToken();

            UserInfo userInfo = new UserInfo();

            // Extract only essential claims - no PII
            userInfo.setUserId(jwt.getClaimAsString("sub")); // Subject claim is the user ID
            userInfo.setUsername(jwt.getClaimAsString("preferred_username"));
            userInfo.setRoles(extractRoles(jwt));

            return userInfo;
        }

        return null;
    }

    /**
     * Extracts roles from the JWT token's realm_access claim
     *
     * @param jwt The JWT token
     * @return List of role names
     */
    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Jwt jwt) {
        List<String> roles = new ArrayList<>();

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof List) {
                roles = (List<String>) rolesObj;
            }
        }

        return roles;
    }
}
