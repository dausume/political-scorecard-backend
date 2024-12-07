package com.asc.politicalscorecard.objects.location;

public class State {
    private String id; // Unique identifier for the state
    private String stateName; // Name of the state
    private String geoLocationId; // GeoLocation ID associated with the state
    private String parentNationId; // Parent nation's ID

    // Default constructor
    public State() {}

    // Constructor for minimal initialization
    public State(String id, String stateName) {
        this.id = id;
        this.stateName = stateName;
    }

    // Constructor with parentNationId
    public State(String id, String stateName, String parentNationId) {
        this.id = id;
        this.stateName = stateName;
        this.parentNationId = parentNationId;
    }

    // Full constructor with all fields
    public State(String id, String stateName, String geoLocationId, String parentNationId) {
        this.id = id;
        this.stateName = stateName;
        this.geoLocationId = geoLocationId;
        this.parentNationId = parentNationId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getStateName() {
        return stateName;
    }

    public String getGeoLocationId() {
        return geoLocationId;
    }

    public String getParentNationId() {
        return parentNationId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public void setGeoLocationId(String geoLocationId) {
        this.geoLocationId = geoLocationId;
    }

    public void setParentNationId(String parentNationId) {
        this.parentNationId = parentNationId;
    }

    @Override
    public String toString() {
        return "State{" +
                "id='" + id + '\'' +
                ", stateName='" + stateName + '\'' +
                ", geoLocationId='" + geoLocationId + '\'' +
                ", parentNationId='" + parentNationId + '\'' +
                '}';
    }
}
