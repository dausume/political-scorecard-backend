package com.asc.politicalscorecard.objects.location;

public class SchoolDistrict {
    private String id;
    private String geoJsonId;
    private String parentCountyId; // Logical parent: County

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
    public String getParentCountyId() {
        return parentCountyId;
    }
    public void setParentCountyId(String parentCountyId) {
        this.parentCountyId = parentCountyId;
    }
}
