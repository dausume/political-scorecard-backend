package com.asc.politicalscorecard.json.serializer.nationserializers;

import com.asc.politicalscorecard.databases.daos.planetdaos.PlanetDAO;
import com.asc.politicalscorecard.json.dtos.nationdto.NationDTO;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

public class NationSerializer extends JsonSerializer<NationDTO> {

    private final PlanetDAO planetDAO;

    // Constructor injection of the PlanetDAO to allow access to the database
    @Autowired
    public NationSerializer(PlanetDAO planetDAO) {
        this.planetDAO = planetDAO;
    }

    @Override
    public void serialize(NationDTO nationDTO, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        System.out.println("Inside nation serializer");

        // Start writing the JSON object
        gen.writeStartObject();
        
        // Write the ID field of the NationDTO
        gen.writeStringField("id", nationDTO.getId());
        
        // Write the nationName field of the NationDTO
        gen.writeStringField("nationName", nationDTO.getNationName());

        // End writing the JSON object
        gen.writeEndObject();
    }
}
