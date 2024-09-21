package com.asc.politicalscorecard.json.dtos.planetdto;

import com.asc.politicalscorecard.json.deserializer.PlanetDeserializer;
import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.json.serializer.PlanetSerializer;
import com.asc.politicalscorecard.objects.location.Planet;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

@JsonSerialize(using = PlanetSerializer.class)
@JsonDeserialize(using = PlanetDeserializer.class)
public class PlanetDTO extends AbstractDTO {
    private String planetName;

    public static PlanetDTO fromExistingPlanet(String id, String planetName) {
        return new PlanetDTO(id, planetName);
    }
    
    public static PlanetDTO fromNewPlanet(String planetName) {
        return new PlanetDTO(planetName);
    }
    
    public static PlanetDTO fromPlanetEntity(Planet planet) {
        return new PlanetDTO(planet.getId(), planet.getPlanetName());
    }

    public PlanetDTO() {
        // No-argument constructor
    }
    

    private PlanetDTO(String id, String planetName) {
        super.setId(id);
        this.planetName = planetName;
    }
    
    private PlanetDTO(String planetName) {
        super.setId();
        this.planetName = planetName;
    }

    // Getter and Setter for planetName
    public String getPlanetName() {
        System.out.println("getting planet name in DTO");
        return planetName;
    }

    public void setPlanetName(String planetName) {
        System.out.println("setting planet value in DTO");
        this.planetName = planetName;
    }

    public Planet toEntity() {
        if(this.getId() == null) // Used in the case where you are moving around objects that have not been put into the database.
        {
            return new Planet(getId(), planetName);
        }
        else // Used in the case where you are moving around objects that are not yet in the database.
        {
            return new Planet(planetName);
        }
    }

    @Override
    public String toString() {
        return "PlanetDTO [id=" + getId() + ", planetName=" + planetName + "]";
    }
}
