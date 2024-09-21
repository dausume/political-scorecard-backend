package com.asc.politicalscorecard.json.deserializer;


import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class PlanetDeserializer extends JsonDeserializer<PlanetDTO> {

    @Override
    public PlanetDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        String id = node.has("id") ? node.get("id").asText(null) : null;
        String planetName = node.has("planetName") ? node.get("planetName").asText(null) : null;

        if (planetName == null) {
            throw new IOException("Missing required field: planetName");
        }

        if (id != null) {
            return PlanetDTO.fromExistingPlanet(id, planetName);
        } else {
            return PlanetDTO.fromNewPlanet(planetName);
        }
    }
}