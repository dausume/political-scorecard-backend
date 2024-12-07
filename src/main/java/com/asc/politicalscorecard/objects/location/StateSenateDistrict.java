package com.asc.politicalscorecard.objects.location;

import java.time.LocalDate;

public class StateSenateDistrict {
    private String id;
    private String geoJsonId;
    private String parentStateId; // Logical parent: State
    private LocalDate validFromDate; // When this district is valid
    private LocalDate validToDate;   // When this district is no longer valid

    // Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getGeoJsonId() {
        return geoJsonId;
    }
    public void setGeoJsonId(String geoJsonId) {
        this.geoJsonId = geoJsonId;
    }
    public String getParentStateId() {
        return parentStateId;
    }
    public void setParentStateId(String parentStateId) {
        this.parentStateId = parentStateId;
    }
    public LocalDate getValidFromDate() {
        return validFromDate;
    }
    public void setValidFromDate(LocalDate validFromDate) {
        this.validFromDate = validFromDate;
    }
    public LocalDate getValidToDate() {
        return validToDate;
    }
    public void setValidToDate(LocalDate validToDate) {
        this.validToDate = validToDate;
    }
}
