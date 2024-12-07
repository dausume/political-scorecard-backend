package com.asc.politicalscorecard.json.dtos.statedto.stategeolocationdto;

public class StateGeoLocationDTO {

    private String id;         // Unique identifier for the state, e.g., "state:CA"
    private String geoJson;    // GeoJSON string representing the state's geographic data

    // Default constructor
    public StateGeoLocationDTO() {
    }

    // Constructor with all fields
    public StateGeoLocationDTO(String id, String geoJson) {
        this.id = id;
        this.geoJson = geoJson;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGeoJson() {
        return geoJson;
    }

    public void setGeoJson(String geoJson) {
        this.geoJson = geoJson;
    }

    // Optional: Override toString for easier debugging
    @Override
    public String toString() {
        return "StateGeoLocationDTO{" +
                "id='" + id + '\'' +
                ", geoJson='" + geoJson + '\'' +
                '}';
    }
}
