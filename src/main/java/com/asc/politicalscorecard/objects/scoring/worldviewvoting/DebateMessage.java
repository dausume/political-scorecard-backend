package com.asc.politicalscorecard.objects.scoring.worldviewvoting;

import java.time.LocalDateTime;

/**
 * Domain entity representing a message in an election debate/discussion.
 * Messages are tied to specific WorldviewElections and display only usernames.
 */
public class DebateMessage {
    private String id;
    private String electionId;
    private String userId;
    private String username;
    private String message;
    private LocalDateTime timestamp;
    private Boolean edited;
    private LocalDateTime editedAt;
    private Boolean deleted;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor
     */
    public DebateMessage() {
        this.edited = false;
        this.deleted = false;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with required fields
     */
    public DebateMessage(String id, String electionId, String userId, String username, String message) {
        this();
        this.id = id;
        this.electionId = electionId;
        this.userId = userId;
        this.username = username;
        this.message = message;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getElectionId() {
        return electionId;
    }

    public void setElectionId(String electionId) {
        this.electionId = electionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getEdited() {
        return edited;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "DebateMessage{" +
                "id='" + id + '\'' +
                ", electionId='" + electionId + '\'' +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                ", edited=" + edited +
                ", deleted=" + deleted +
                '}';
    }
}
