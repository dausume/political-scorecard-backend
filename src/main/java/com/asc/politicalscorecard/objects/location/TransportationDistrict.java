package com.asc.politicalscorecard.objects.location;

public class TransportationDistrict {
    private String id;
    private String geoJsonId;
    private String parentStateId; // Logical parent: State

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
}