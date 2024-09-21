package com.asc.politicalscorecard.objects.geolocation;

public class MapLayer {
    private String id;

    private String layerName;

    private String geoJson;

    // Constructor
    public MapLayer(String id, String layerName, String geoJson) {
        this.id = id;
        this.layerName = layerName;
        this.geoJson = geoJson;
    }

    // Getter
    public String getId() {
        return id;
    }

    // Getter
    public String getLayerName() {
        return layerName;
    }

    // Getter
    public String getGeoJson() {
        return geoJson;
    }
}