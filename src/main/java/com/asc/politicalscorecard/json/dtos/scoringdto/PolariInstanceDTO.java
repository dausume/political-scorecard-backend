package com.asc.politicalscorecard.json.dtos.scoringdto;

import java.util.UUID;

/**
 * One registered Polari instance — the PSC-side registry that lets a
 * group designate an instance as its authoritative data source.
 * Previously the backend knew exactly one Polari (app.polari.api-url);
 * that instance is seeded as the default row.
 */
public class PolariInstanceDTO {

    private String id;
    private String name;
    private String baseUrl;
    private String description;
    private String status; // 'active' | 'retired'
    private String createdAt;

    public PolariInstanceDTO() {
    }

    public PolariInstanceDTO(String id, String name, String baseUrl,
                             String description, String status) {
        this.id = id;
        this.name = name;
        this.baseUrl = baseUrl;
        this.description = description;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId() {
        this.id = UUID.randomUUID().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
