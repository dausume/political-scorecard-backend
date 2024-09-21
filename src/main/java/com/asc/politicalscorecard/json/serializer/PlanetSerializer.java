package com.asc.politicalscorecard.json.serializer;

import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PlanetSerializer extends JsonSerializer<PlanetDTO> {

    @Override
    public void serialize(PlanetDTO planetDTO, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", planetDTO.getId());
        gen.writeStringField("planetName", planetDTO.getPlanetName());
        gen.writeEndObject();
    }
}
