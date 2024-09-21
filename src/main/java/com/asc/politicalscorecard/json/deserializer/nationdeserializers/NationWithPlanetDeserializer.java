package com.asc.politicalscorecard.json.deserializer.nationdeserializers;

import com.asc.politicalscorecard.json.dtos.nationdto.NationWithPlanetDTO;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class NationWithPlanetDeserializer extends JsonDeserializer<NationWithPlanetDTO> {

    @Override
    public NationWithPlanetDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        String id = node.has("id") ? node.get("id").asText(null) : null;
        String nationName = node.has("nationName") ? node.get("nationName").asText(null) : null;
        
        // Deserialize the homePlanet object
        JsonNode homePlanetNode = node.get("homePlanet");
        PlanetDTO homePlanet = jp.getCodec().treeToValue(homePlanetNode, PlanetDTO.class);

        if (nationName == null) {
            throw new IOException("Missing required field: nationName");
        }
        if (homePlanet == null) {
            throw new IOException("Missing required field: homePlanet");
        }

        if (id != null) {
            return new NationWithPlanetDTO(id, nationName, homePlanet);
        } else {
            return new NationWithPlanetDTO(nationName, homePlanet);
        }
    }
}