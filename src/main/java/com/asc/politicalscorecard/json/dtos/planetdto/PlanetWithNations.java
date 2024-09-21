package com.asc.politicalscorecard.json.dtos.planetdto;
import java.util.List;

import com.asc.politicalscorecard.json.dtos.nationdto.NationDTO;
import com.asc.politicalscorecard.objects.location.Planet;

public class PlanetWithNations extends PlanetDTO {
    private String planetName;
    private List<NationDTO> nations;

    public static PlanetWithNations fromExistingPlanet(String id, String planetName) {
        return new PlanetWithNations(id, planetName);
    }
    
    public static PlanetWithNations fromNewPlanet(String planetName) {
        return new PlanetWithNations(planetName);
    }
    
    public static PlanetWithNations fromPlanetEntity(Planet planet) {
        return new PlanetWithNations(planet.getId(), planet.getPlanetName());
    }

    public PlanetWithNations() {
        // No-argument constructor
    }
    
    private PlanetWithNations(String id, String planetName) {
        super.setId(id);
        this.planetName = planetName;
    }
    
    private PlanetWithNations(String planetName) {
        super.setId();
        this.planetName = planetName;
    }
}
