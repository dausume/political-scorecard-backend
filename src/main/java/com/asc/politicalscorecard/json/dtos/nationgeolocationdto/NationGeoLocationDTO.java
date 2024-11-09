package com.asc.politicalscorecard.json.dtos.nationgeolocationdto;

public class NationGeoLocationDTO {
    private String id;
    private String geoJson;

    public NationGeoLocationDTO(String id, String geoJson) {
        this.id = id;
        this.geoJson = geoJson;
    }

    public NationGeoLocationDTO(String geoJson) {
        this.geoJson = geoJson;
    }

    public String getId() {
        return id;
    }

    public String getGeoJson() {
        return geoJson;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGeoJson(String geoJson) {
        this.geoJson = geoJson;
    }
}