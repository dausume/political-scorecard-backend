package com.asc.politicalscorecard.objects.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User information and authentication details.
 * Aligned with frontend AuthUser model.
 * Excludes sensitive information like passwords, first and last names, and email.
 */
public class UserInfo {
    // Unique user identifier (matches frontend 'id' field)
    private String id;

    // Username for login
    private String username;

    // User roles (e.g., "admin", "user", "moderator")
    private List<String> roles;

    // User permissions (specific permission strings)
    private List<String> permissions;

    // User preferences (flexible key-value storage)
    private Map<String, Object> preferences;

    // Legacy field for backward compatibility
    @Deprecated
    private String userId; // Alias for 'id'

    // Constructors
    public UserInfo() {
        this.roles = new ArrayList<>();
        this.permissions = new ArrayList<>();
        this.preferences = new HashMap<>();
    }

    public UserInfo(String id, String username, String email, String firstName, String lastName,
                   List<String> roles, List<String> permissions, Map<String, Object> preferences) {
        this.id = id;
        this.userId = id; // Keep in sync
        this.username = username;
        this.roles = roles != null ? roles : new ArrayList<>();
        this.permissions = permissions != null ? permissions : new ArrayList<>();
        this.preferences = preferences != null ? preferences : new HashMap<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.userId = id; // Keep in sync
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public Map<String, Object> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, Object> preferences) {
        this.preferences = preferences;
    }

    // Legacy getter/setter for backward compatibility
    @Deprecated
    public String getUserId() {
        return userId;
    }

    @Deprecated
    public void setUserId(String userId) {
        this.userId = userId;
        this.id = userId; // Keep in sync
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
