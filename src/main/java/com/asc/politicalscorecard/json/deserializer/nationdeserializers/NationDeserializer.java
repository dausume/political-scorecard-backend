package com.asc.politicalscorecard.json.deserializer.nationdeserializers;

import com.asc.politicalscorecard.json.dtos.nationdto.NationDTO;
import com.asc.politicalscorecard.objects.location.Planet;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class NationDeserializer extends JsonDeserializer<NationDTO> {

    @Override
    public NationDTO deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        String id = node.has("id") ? node.get("id").asText(null) : null;

        String nationName = node.has("nationName") ? node.get("nationName").asText(null) : null;

        String homePlanetId = node.has("homePlanetId") ? node.get("homePlanetId").asText(null) : null;

        // Ensure that the required fields are not null
        if (nationName == null) { // Ensure that the nationName field is not null
            throw new IOException("Missing required field: nationName");
        }
        if (homePlanetId == null) { // Ensure that at least one of the two fields is not null
            throw new IOException("Missing required field: homePlanetId");
        }

        // Return the appropriate NationDTO object using the appropriate creation method based on the kind of 
        // json we were passed. (refer to prior conditionals for more information)
        if (id != null) {
            return new NationDTO(id, nationName, homePlanetId);
        } else {
            return new NationDTO(nationName, homePlanetId);
        }
    }
}
