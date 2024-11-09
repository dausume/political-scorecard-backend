package com.asc.politicalscorecard.objects.location;

public abstract class AbstractLocation {

    protected String id;
    protected String baseGeoJsonRef; // Reference to the baseline GeoJson object for this location in the database.

    public AbstractLocation() {}

    public AbstractLocation(String id) {
        this.id = id;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaseGeoJsonRef() {
        return baseGeoJsonRef;
    }

    public void setBaseGeoJson(String baseGeoJsonRef) {
        this.baseGeoJsonRef = baseGeoJsonRef;
    }

}