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

import com.asc.politicalscorecard.controllers.responses.ApiResponse;

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

    public ApiResponse<NationWithPlanetDTO> findById(String id) {
        // Fetch the nation using the NationDAO
        try {
            ApiResponse<NationDTO> nationResponse = nationDAO.read(id);
            
            if (!nationResponse.isSuccess() || nationResponse.getData() == null) {
                throw new RuntimeException("Nation not found with ID: " + id);
            }
    
            NationDTO nation = nationResponse.getData();
            
            // Fetch the PlanetDTO using the PlanetDAO
            ApiResponse<PlanetDTO> planetResponse = planetDAO.read(nation.getHomePlanetId());
            
            if (!planetResponse.isSuccess() || planetResponse.getData() == null) {
                throw new RuntimeException("Planet not found for nation with ID: " + nation.getId());
            }
            
            PlanetDTO homePlanet = planetResponse.getData();
            
            // Create and return the composite DTO with the Nation containing its home planet info
            NationWithPlanetDTO dto = new NationWithPlanetDTO(nation.getId(), nation.getNationName(), homePlanet);
            
            // Correctly create and return the ApiResponse
            return new ApiResponse<NationWithPlanetDTO>(true, "Nation with planet found successfully", dto);
        } 
        catch (Exception e) {
            System.out.println("Error in NationWithPlanetDAO: " + e.getMessage());
            return new ApiResponse<NationWithPlanetDTO>(false, "Error finding nation with planet: " + e.getMessage(), null);
        }
    }
    
}
