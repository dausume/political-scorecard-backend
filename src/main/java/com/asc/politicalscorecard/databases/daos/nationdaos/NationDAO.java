package com.asc.politicalscorecard.databases.daos.nationdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.databases.daos.planetdaos.PlanetDAO;
import com.asc.politicalscorecard.exceptions.apiuserexceptions.InvalidInputException;
import com.asc.politicalscorecard.json.dtos.nationdto.NationDTO;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.asc.politicalscorecard.objects.location.Planet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class NationDAO extends AbstractDAO<NationDTO> {

    private static final Logger logger = Logger.getLogger(NationDAO.class.getName());
    private final JdbcClient jdbcClient;

    @Autowired
    public NationDAO(@Qualifier("locationJdbcClient") JdbcClient jdbcClient, PlanetDAO planetDAO) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public ApiResponse<NationDTO> create(NationDTO dto) {
        String query = "INSERT INTO nation (id, nation_name, home_planet) VALUES (?, ?, ?)";
        try {
            int rowsAffected = jdbcClient.sql(query)
                      .params(List.of(dto.getId(), dto.getNationName(), dto.getHomePlanetId()))
                      .update();
            if (rowsAffected > 0) {
                return new ApiResponse<NationDTO>(true, "Nation created successfully", dto);
            } else {
                return new ApiResponse<NationDTO>(false, "Failed to create nation in the NationDAO.", dto);
            }
        } catch (NullPointerException e) 
        {
            throw new InvalidInputException(e.getMessage());
        } catch (Exception e) 
        {
            logger.log(Level.SEVERE, "Error creating nation: ", e);
            return new ApiResponse<NationDTO>(false, "Error creating nation: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<NationDTO> read(String id) {
        String query = "SELECT * FROM nation WHERE id = ?";
        try {
            List<NationDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(NationDTO.class)
                    .list();
            if (dtoList.size() == 1) {
                return new ApiResponse<NationDTO>(true, "Nation found successfully", dtoList.get(0));
            } else {
                String message = dtoList.isEmpty() ? "No nation found with the given ID." : "Multiple nations found with the same ID.";
                logger.log(Level.SEVERE, message);
                return new ApiResponse<NationDTO>(false, message, null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading nation: ", e);
            return new ApiResponse<NationDTO>(false, "Error reading nation: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<NationDTO>> readAll() {
        String query = "SELECT * FROM nation";
        try {
            List<NationDTO> dtoList = jdbcClient.sql(query).query(NationDTO.class).list();
            return new ApiResponse<List<NationDTO>>(true, "All nations retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all nations: ", e);
            return new ApiResponse<List<NationDTO>>(false, "Error reading all nations: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<NationDTO> update(NationDTO dto) {
        String query = "UPDATE nation SET nation_name = ?, home_planet = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(dto.getNationName(), dto.getHomePlanetId(), dto.getId()))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<NationDTO>(true, "Nation updated successfully", dto);
            } else {
                return new ApiResponse<NationDTO>(false, "Failed to update nation.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating nation: ", e);
            return new ApiResponse<NationDTO>(false, "Error updating nation: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<NationDTO> delete(String id) {
        String query = "DELETE FROM nation WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();
            if (rowsAffected > 0) {
                return new ApiResponse<NationDTO>(true, "Nation deleted successfully", null);
            } else {
                return new ApiResponse<NationDTO>(false, "Failed to delete nation.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting nation: ", e);
            return new ApiResponse<>(false, "Error deleting nation: " + e.getMessage(), null);
        }
    }

}
