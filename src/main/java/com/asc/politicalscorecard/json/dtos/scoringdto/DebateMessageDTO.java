package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.objects.scoring.worldviewvoting.DebateMessage;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for DebateMessage.
 * Used for API communication and data transfer.
 */
public class DebateMessageDTO extends AbstractDTO {

    private String electionId;
    private String userId;
    private String username;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private Boolean edited;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime editedAt;

    private Boolean deleted;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deletedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public DebateMessageDTO() {
        this.edited = false;
        this.deleted = false;
    }

    public DebateMessageDTO(String id, String electionId, String userId, String username, String message) {
        this();
        this.setId(id);
        this.electionId = electionId;
        this.userId = userId;
        this.username = username;
        this.message = message;
    }

    // Getters and Setters

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

    // Convert DTO to Entity
    @Override
    public DebateMessage toEntity() {
        DebateMessage message = new DebateMessage();
        message.setId(this.getId());
        message.setElectionId(this.electionId);
        message.setUserId(this.userId);
        message.setUsername(this.username);
        message.setMessage(this.message);
        message.setTimestamp(this.timestamp);
        message.setEdited(this.edited);
        message.setEditedAt(this.editedAt);
        message.setDeleted(this.deleted);
        message.setDeletedAt(this.deletedAt);
        message.setCreatedAt(this.createdAt);
        message.setUpdatedAt(this.updatedAt);
        return message;
    }

    // Convert Entity to DTO
    public static DebateMessageDTO fromEntity(DebateMessage message) {
        if (message == null) {
            return null;
        }
        DebateMessageDTO dto = new DebateMessageDTO();
        dto.setId(message.getId());
        dto.setElectionId(message.getElectionId());
        dto.setUserId(message.getUserId());
        dto.setUsername(message.getUsername());
        dto.setMessage(message.getMessage());
        dto.setTimestamp(message.getTimestamp());
        dto.setEdited(message.getEdited());
        dto.setEditedAt(message.getEditedAt());
        dto.setDeleted(message.getDeleted());
        dto.setDeletedAt(message.getDeletedAt());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        return dto;
    }

    @Override
    public String toString() {
        return "DebateMessageDTO{" +
                "id='" + getId() + '\'' +
                ", electionId='" + electionId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", edited=" + edited +
                ", editedAt=" + editedAt +
                ", deleted=" + deleted +
                ", deletedAt=" + deletedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
