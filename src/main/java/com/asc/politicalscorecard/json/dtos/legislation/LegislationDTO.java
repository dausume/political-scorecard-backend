package com.asc.politicalscorecard.json.dtos.legislation;

import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.objects.legislation.Legislation;

public class LegislationDTO extends AbstractDTO {

    private String title;
    private String description;
    private String legislativeBodyId;
    private String validFromDate;
    private String validToDate;
    private String legislationText;
    private String url;
    private String status;
    private String createdBy;
    private String createdAt;
    private String updatedAt;

    public LegislationDTO() {}

    public LegislationDTO(String id, String title, String description, String legislativeBodyId,
                          String validFromDate, String validToDate, String legislationText,
                          String url, String status, String createdBy, String createdAt, String updatedAt) {
        this.setId(id);
        this.title = title;
        this.description = description;
        this.legislativeBodyId = legislativeBodyId;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
        this.legislationText = legislationText;
        this.url = url;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLegislativeBodyId() { return legislativeBodyId; }
    public void setLegislativeBodyId(String legislativeBodyId) { this.legislativeBodyId = legislativeBodyId; }

    public String getValidFromDate() { return validFromDate; }
    public void setValidFromDate(String validFromDate) { this.validFromDate = validFromDate; }

    public String getValidToDate() { return validToDate; }
    public void setValidToDate(String validToDate) { this.validToDate = validToDate; }

    public String getLegislationText() { return legislationText; }
    public void setLegislationText(String legislationText) { this.legislationText = legislationText; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public Legislation toEntity() {
        Legislation legislation = new Legislation();
        legislation.setId(this.getId());
        legislation.setTitle(this.title);
        legislation.setDescription(this.description);
        legislation.setLegislativeBodyId(this.legislativeBodyId);
        legislation.setValidFromDate(this.validFromDate);
        legislation.setValidToDate(this.validToDate);
        legislation.setLegislationText(this.legislationText);
        legislation.setUrl(this.url);
        legislation.setStatus(this.status);
        legislation.setCreatedBy(this.createdBy);
        legislation.setCreatedAt(this.createdAt);
        legislation.setUpdatedAt(this.updatedAt);
        return legislation;
    }

    public static LegislationDTO fromEntity(Legislation legislation) {
        if (legislation == null) return null;
        LegislationDTO dto = new LegislationDTO();
        dto.setId(legislation.getId());
        dto.setTitle(legislation.getTitle());
        dto.setDescription(legislation.getDescription());
        dto.setLegislativeBodyId(legislation.getLegislativeBodyId());
        dto.setValidFromDate(legislation.getValidFromDate());
        dto.setValidToDate(legislation.getValidToDate());
        dto.setLegislationText(legislation.getLegislationText());
        dto.setUrl(legislation.getUrl());
        dto.setStatus(legislation.getStatus());
        dto.setCreatedBy(legislation.getCreatedBy());
        dto.setCreatedAt(legislation.getCreatedAt());
        dto.setUpdatedAt(legislation.getUpdatedAt());
        return dto;
    }

    @Override
    public String toString() {
        return "LegislationDTO{" +
                "id='" + getId() + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
