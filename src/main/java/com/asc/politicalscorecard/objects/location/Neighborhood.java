package com.asc.politicalscorecard.objects.location;

public class Neighborhood {
    private String id;
    private String geoJsonId;
    private String parentMunicipalityId; // Logical parent: Municipality

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
    public String getParentMunicipalityId() {
        return parentMunicipalityId;
    }
    public void setParentMunicipalityId(String parentMunicipalityId) {
        this.parentMunicipalityId = parentMunicipalityId;
    }
}
