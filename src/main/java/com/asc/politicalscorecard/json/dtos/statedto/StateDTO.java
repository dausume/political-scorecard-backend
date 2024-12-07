package com.asc.politicalscorecard.json.dtos.statedto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.asc.politicalscorecard.json.deserializer.statedeserializers.StateDeserializer;
import com.asc.politicalscorecard.json.serializer.stateserializers.StateSerializer;

@JsonSerialize(using = StateSerializer.class)
@JsonDeserialize(using = StateDeserializer.class)
public class StateDTO {
    
    private String id; // Unique identifier for the state
    private String stateName; // Name of the state
    private String geoLocationId; // Identifier for the GeoJSON data of the state
    private String parentNationId; // Identifier for the parent nation

    public StateDTO() {}

    public StateDTO(String id, String stateName, String parentNationId) 
    {
        this.id = id;
        this.stateName = stateName;
        this.parentNationId = parentNationId;
    }

    public StateDTO(String id, String stateName, String geoLocationId, String parentNationId) {
        this.id = id;
        this.stateName = stateName;
        this.geoLocationId = geoLocationId;
        this.parentNationId = parentNationId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getGeoLocationId() {
        return geoLocationId;
    }

    public void setGeoLocationId(String geoLocationId) {
        this.geoLocationId = geoLocationId;
    }

    public String getParentNationId() {
        return parentNationId;
    }

    public void setParentNationId(String parentNationId) {
        this.parentNationId = parentNationId;
    }

    @Override
    public String toString() {
        return "StateDTO{" +
                "id='" + id + '\'' +
                ", stateName='" + stateName + '\'' +
                ", geoLocationId='" + geoLocationId + '\'' +
                ", parentNationId='" + parentNationId + '\'' +
                '}';
    }
}