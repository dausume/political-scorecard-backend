package com.asc.politicalscorecard.json.dtos.nationdto;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;


// Defines a data transfer object for a nation which includes a reference to the planet object for the planet it is on
// instead of the planet id.
public class NationWithPlanetDTO extends NationDTO {
    private PlanetDTO homePlanet;

    public NationWithPlanetDTO() {
        // No-argument constructor
    }

    // For existing Nation Objects.
    public NationWithPlanetDTO(String id, String nationName, PlanetDTO homePlanet) {
        super(id, nationName, homePlanet.getId());
        this.homePlanet = homePlanet;
    }

    // For new Nation objects.
    public NationWithPlanetDTO(String nationName, PlanetDTO homePlanet) {
        super(nationName, homePlanet.getId());
        this.homePlanet = homePlanet;
    }

    // Getter and Setter for homePlanet
    public PlanetDTO getHomePlanet() {
        return homePlanet;
    }

    public void setHomePlanet(PlanetDTO homePlanet) {
        this.homePlanet = homePlanet;
    }
}
