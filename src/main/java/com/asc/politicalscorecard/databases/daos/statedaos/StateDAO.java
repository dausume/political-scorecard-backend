package com.asc.politicalscorecard.databases.daos.statedaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.statedto.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class StateDAO extends AbstractDAO<StateDTO> {

    private static final Logger logger = Logger.getLogger(StateDAO.class.getName());
    private final JdbcClient jdbcClient;

    @Autowired
    public StateDAO(@Qualifier("locationJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public ApiResponse<StateDTO> create(StateDTO dto) {
        String query = "INSERT INTO state (id, state_name, geo_location_id, parent_nation_id) VALUES (?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(dto.getId(), dto.getStateName(), dto.getGeoLocationId(), dto.getParentNationId()))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "State created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create state.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating state: ", e);
            return new ApiResponse<>(false, "Error creating state: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<StateDTO> read(String id) {
        String query = "SELECT * FROM state WHERE id = ?";
        try {
            List<StateDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(StateDTO.class)
                    .list();
            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "State found successfully", dtoList.get(0));
            } else {
                String message = dtoList.isEmpty() ? "No state found with the given ID." : "Multiple states found with the same ID.";
                logger.log(Level.SEVERE, message);
                return new ApiResponse<>(false, message, null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading state: ", e);
            return new ApiResponse<>(false, "Error reading state: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<StateDTO>> readAll() {
        String query = "SELECT * FROM state";
        try {
            List<StateDTO> dtoList = jdbcClient.sql(query).query(StateDTO.class).list();
            return new ApiResponse<>(true, "All states retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all states: ", e);
            return new ApiResponse<>(false, "Error reading all states: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<StateDTO> update(StateDTO dto) {
        String query = "UPDATE state SET state_name = ?, geo_location_id = ?, parent_nation_id = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(dto.getStateName(), dto.getGeoLocationId(), dto.getParentNationId(), dto.getId()))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "State updated successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to update state.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating state: ", e);
            return new ApiResponse<>(false, "Error updating state: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<StateDTO> delete(String id) {
        String query = "DELETE FROM state WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "State deleted successfully", null);
            } else {
                return new ApiResponse<>(false, "Failed to delete state.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting state: ", e);
            return new ApiResponse<>(false, "Error deleting state: " + e.getMessage(), null);
        }
    }
}