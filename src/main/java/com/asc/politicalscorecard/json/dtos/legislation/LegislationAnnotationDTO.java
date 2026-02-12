package com.asc.politicalscorecard.json.dtos.legislation;

import com.asc.politicalscorecard.json.dtos.AbstractDTO;

public class LegislationAnnotationDTO extends AbstractDTO {

    private String legislationId;
    private String userId;
    private String groupId;
    private String annotationType;
    private String bodyJson;
    private String targetJson;
    private String createdAt;
    private String updatedAt;

    public LegislationAnnotationDTO() {}

    public LegislationAnnotationDTO(String id, String legislationId, String userId, String groupId,
                                    String annotationType, String bodyJson, String targetJson,
                                    String createdAt, String updatedAt) {
        this.setId(id);
        this.legislationId = legislationId;
        this.userId = userId;
        this.groupId = groupId;
        this.annotationType = annotationType;
        this.bodyJson = bodyJson;
        this.targetJson = targetJson;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getLegislationId() { return legislationId; }
    public void setLegislationId(String legislationId) { this.legislationId = legislationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getAnnotationType() { return annotationType; }
    public void setAnnotationType(String annotationType) { this.annotationType = annotationType; }

    public String getBodyJson() { return bodyJson; }
    public void setBodyJson(String bodyJson) { this.bodyJson = bodyJson; }

    public String getTargetJson() { return targetJson; }
    public void setTargetJson(String targetJson) { this.targetJson = targetJson; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public Object toEntity() {
        return this;
    }

    @Override
    public String toString() {
        return "LegislationAnnotationDTO{" +
                "id='" + getId() + '\'' +
                ", legislationId='" + legislationId + '\'' +
                ", annotationType='" + annotationType + '\'' +
                '}';
    }
}
