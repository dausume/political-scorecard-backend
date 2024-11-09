package com.asc.politicalscorecard.databases.daos.planetdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.nationdto.NationDTO;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.asc.politicalscorecard.objects.location.Planet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.jdbc.core.simple.JdbcClient;

import org.springframework.stereotype.Repository;

@Repository
public class PlanetDAO extends AbstractDAO<PlanetDTO> {
    private static final Logger logger = Logger.getLogger(PlanetDAO.class.getName());

    private JdbcClient jdbcClient;

    @Autowired
    public PlanetDAO(@Qualifier("locationJdbcClient") JdbcClient jdbcClient) 
    {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public ApiResponse<PlanetDTO> create(PlanetDTO dto) {
        System.out.println("In create Planet in PlanetDAO");
        String query = "INSERT INTO planet (id, planet_name) VALUES (?, ?)";
        try {
            int rowsAffected = jdbcClient.sql(query)
                      .params(List.of(dto.getId(), dto.getPlanetName()))
                      .update();
            if (rowsAffected > 0) 
            {
                return new ApiResponse<PlanetDTO>(true, "Nation created successfully", dto);
            } 
            else 
            {
                return new ApiResponse<PlanetDTO>(false, "Failed to create nation in the NationDAO.", dto);
            }
        } catch (Exception e) 
        {
            logger.log(Level.SEVERE, "Error creating planet: ", e);
            return new ApiResponse<PlanetDTO>(false, "Error creating nation: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<PlanetDTO> read(String id) {
        System.out.println("Reading Planet object with id : " + id);
        String query = "SELECT * FROM planet WHERE id = ?";
        try {
            PlanetDTO dto = jdbcClient.sql(query)
                                     .params(List.of(id))
                                     .query(PlanetDTO.class)
                                     .single();
            if (dto != null) {
                return new ApiResponse<PlanetDTO>(true, "Planet found successfully", dto);
            } else {
                return new ApiResponse<PlanetDTO>(false, "Planet not found with the given ID.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading planet: ", e);
            return new ApiResponse<PlanetDTO>(false, "Error reading planet: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<PlanetDTO>> readAll() {
        String query = "SELECT * FROM planet";
        try {
            List<PlanetDTO> allPlanets = jdbcClient.sql(query)
                                                   .query(PlanetDTO.class)
                                                   .list();
            return new ApiResponse<List<PlanetDTO>>(true, "All planets retrieved successfully", allPlanets);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all planets: ", e);
            return new ApiResponse<List<PlanetDTO>>(false, "Error reading all planets: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<PlanetDTO> update(PlanetDTO dto) {
        String query = "UPDATE planet SET planet_name = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query)
                                         .params(List.of(dto.getPlanetName(), dto.getId()))
                                         .update();
            if (rowsAffected > 0) {
                return new ApiResponse<PlanetDTO>(true, "Planet updated successfully", dto);
            } else {
                return new ApiResponse<PlanetDTO>(false, "Failed to update planet.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating planet: ", e);
            return new ApiResponse<PlanetDTO>(false, "Error updating planet: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<PlanetDTO> delete(String id) {
        String query = "DELETE FROM planet WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query)
                                         .params(List.of(id))
                                         .update();
            if (rowsAffected > 0) {
                return new ApiResponse<PlanetDTO>(true, "Planet deleted successfully", null);
            } else {
                return new ApiResponse<PlanetDTO>(false, "Failed to delete planet.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting planet: ", e);
            return new ApiResponse<PlanetDTO>(false, "Error deleting planet: " + e.getMessage(), null);
        }
    }
}
