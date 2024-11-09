package com.asc.politicalscorecard.objects.location;

public class GeoLocation
{    
    private String id;

    private String remoteUrl; // This is a URL that points to the location data. This is useful for when we want to store a reference to the location data, but don't want to store the data itself.

    // Long term if we want the ability to manipulate the geoJson data, we may want to create a GeoJson object that can be manipulated 
    // and then converted to a string for storage in the database.
    private String geoJson; // Must use TEXT type in Table definition to ensure DB can handle length of geoJson text.

    private String locationRefId;

    private String creditSource; // String that details the attribution for the geoJson data, it's creator, and the license under which it is shared.

    public GeoLocation() {}

    public GeoLocation(String id, String geoJson, String locationRefId) {
        this.id = id;
        this.geoJson = geoJson;
        this.locationRefId = locationRefId;
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getGeoJson() {
        return geoJson;
    }

    public String getLocationRefId() {
        return locationRefId;
    }

    // Setters

    public void setId(String id) {
        this.id = id;
    }

    public void setGeoJson(String geoJson) {
        this.geoJson = geoJson;
    }

    public void setLocationRefId(String locationRefId) {
        this.locationRefId = locationRefId;
    }
}