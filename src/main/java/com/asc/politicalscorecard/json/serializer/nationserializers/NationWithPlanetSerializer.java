package com.asc.politicalscorecard.json.serializer.nationserializers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

// import jsongenerator
import com.fasterxml.jackson.core.JsonGenerator;
import com.asc.politicalscorecard.json.serializer.nationserializers.NationSerializer;
import com.asc.politicalscorecard.databases.daos.planetdaos.PlanetDAO;
import com.asc.politicalscorecard.json.dtos.nationdto.NationWithPlanetDTO;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class NationWithPlanetSerializer extends JsonSerializer<NationWithPlanetDTO> {

    private final PlanetDAO planetDAO;

    // Constructor injection of the PlanetDAO to allow access to the database
    @Autowired
    public NationWithPlanetSerializer(PlanetDAO planetDAO) {
        this.planetDAO = planetDAO;
    }

     @Override
    public void serialize(NationWithPlanetDTO nationWithPlanetDTO, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", nationWithPlanetDTO.getId());
        gen.writeStringField("nationName", nationWithPlanetDTO.getNationName());

        // Serialize the homePlanet object
        gen.writeObjectField("homePlanet", nationWithPlanetDTO.getHomePlanet());

        gen.writeEndObject();
    }
}
