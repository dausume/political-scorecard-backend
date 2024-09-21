package com.asc.politicalscorecard.databases.daos.nationdaos;

import com.asc.politicalscorecard.databases.daos.nationdaos.NationDAO;
import com.asc.politicalscorecard.databases.daos.planetdaos.PlanetDAO;

import com.asc.politicalscorecard.json.dtos.nationdto.NationDTO;
import com.asc.politicalscorecard.json.dtos.nationdto.NationWithPlanetDTO;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.asc.politicalscorecard.objects.location.Nation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class NationWithPlanetDAO {

    private final JdbcClient locationjdbcClient;
    private final PlanetDAO planetDAO;
    private final NationDAO nationDAO;

    public NationWithPlanetDAO(@Qualifier("locationJdbcClient") JdbcClient locationjdbcClient, PlanetDAO planetDAO, NationDAO nationDAO) {
        this.locationjdbcClient = locationjdbcClient;
        this.planetDAO = planetDAO;
        this.nationDAO = nationDAO;
    }

    public NationWithPlanetDTO findById(String id) {
        // First, fetch the nation using the NationDAO
        NationDTO nation = nationDAO.read(id);
        // Fetch the PlanetDTO using the PlanetDAO
        PlanetDTO homePlanet = planetDAO.read(nation.getHomePlanetId());
        // Create the composite DTO with the Nation containing it's home planet info.
        return new NationWithPlanetDTO(nation.getId(), nation.getNationName(), homePlanet);
    }
}
