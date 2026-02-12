package com.asc.politicalscorecard.objects.legislation;

import java.util.List;

public class Legislation {
    private String id;

    // Title of the legislation (e.g., "Clean Energy Act 2024").
    private String title;

    // Description or summary of the legislation.
    private String description;

    // Legislative body responsible for the legislation.
    private String legislativeBodyId;

    // GeoLocations where the legislation is applicable.
    private List<String> geoLocationIds;

    // Validity period for the legislation (null for ongoing laws).
    private String validFromDate;
    private String validToDate;

    // Associated policy IDs derived from this legislation.
    private List<String> associatedPolicyIds;

    // Text of the legislation (optional, for full-text analysis).
    private String legislationText;

    // Url reference to the legislation.
    private String url;

    // Lifecycle status: DRAFT, SUBMITTED, APPROVED, LOCKED
    private String status;

    // Audit fields
    private String createdBy;
    private String createdAt;
    private String updatedAt;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLegislativeBodyId() { return legislativeBodyId; }
    public void setLegislativeBodyId(String legislativeBodyId) { this.legislativeBodyId = legislativeBodyId; }

    public List<String> getGeoLocationIds() { return geoLocationIds; }
    public void setGeoLocationIds(List<String> geoLocationIds) { this.geoLocationIds = geoLocationIds; }

    public String getValidFromDate() { return validFromDate; }
    public void setValidFromDate(String validFromDate) { this.validFromDate = validFromDate; }

    public String getValidToDate() { return validToDate; }
    public void setValidToDate(String validToDate) { this.validToDate = validToDate; }

    public List<String> getAssociatedPolicyIds() { return associatedPolicyIds; }
    public void setAssociatedPolicyIds(List<String> associatedPolicyIds) { this.associatedPolicyIds = associatedPolicyIds; }

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
}
