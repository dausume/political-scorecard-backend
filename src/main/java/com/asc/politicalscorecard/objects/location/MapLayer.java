package com.asc.politicalscorecard.objects.location;

public class MapLayer {
    private String id;

    private String layerName;

    // Constructor
    public MapLayer(String id, String layerName) {
        this.id = id;
        this.layerName = layerName;
    }

    // Getter
    public String getId() {
        return id;
    }

    // Getter
    public String getLayerName() {
        return layerName;
    }

}