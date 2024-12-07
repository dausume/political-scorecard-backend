package com.asc.politicalscorecard.json.deserializer.statedeserializers;

import com.asc.politicalscorecard.json.dtos.statedto.StateDTO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class StateDeserializer extends JsonDeserializer<StateDTO> {

    @Override
    public StateDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        // Extract fields from JSON
        String id = node.has("id") ? node.get("id").asText(null) : null;
        String stateName = node.has("stateName") ? node.get("stateName").asText(null) : null;
        String geoLocationId = node.has("geoLocationId") ? node.get("geoLocationId").asText(null) : null;
        String parentNationId = node.has("parentNationId") ? node.get("parentNationId").asText(null) : null;

        // Validate required fields
        if (stateName == null) { 
            throw new IOException("Missing required field: stateName");
        }
        if (parentNationId == null) { 
            throw new IOException("Missing required field: parentNationId");
        }

        // Return the appropriate StateDTO object
        if (id != null) {
            return new StateDTO(id, stateName, geoLocationId, parentNationId);
        } else {
            return new StateDTO(null, stateName, geoLocationId, parentNationId);
        }
    }
}