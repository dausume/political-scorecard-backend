package com.asc.politicalscorecard.json.serializer.stateserializers;

import com.asc.politicalscorecard.json.dtos.statedto.StateDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class StateSerializer extends JsonSerializer<StateDTO> {

    @Override
    public void serialize(StateDTO stateDTO, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        System.out.println("Inside State serializer");

        // Start writing the JSON object
        gen.writeStartObject();
        
        // Write the ID field of the StateDTO
        if (stateDTO.getId() != null) {
            gen.writeStringField("id", stateDTO.getId());
        }

        // Write the stateName field of the StateDTO
        if (stateDTO.getStateName() != null) {
            gen.writeStringField("stateName", stateDTO.getStateName());
        }

        // Write the geoLocationId field of the StateDTO
        if (stateDTO.getGeoLocationId() != null) {
            gen.writeStringField("geoLocationId", stateDTO.getGeoLocationId());
        }

        // Write the parentNationId field of the StateDTO
        if (stateDTO.getParentNationId() != null) {
            gen.writeStringField("parentNationId", stateDTO.getParentNationId());
        }

        // End writing the JSON object
        gen.writeEndObject();
    }
}
