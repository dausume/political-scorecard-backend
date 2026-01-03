package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.ValueMetadataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for ValueMetadata.
 */
@Repository
public class ValueMetadataDAO extends AbstractDAO<ValueMetadataDTO> {

    private static final Logger logger = Logger.getLogger(ValueMetadataDAO.class.getName());
    private final JdbcClient jdbcClient;

    @Autowired
    public ValueMetadataDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public ApiResponse<ValueMetadataDTO> create(ValueMetadataDTO dto) {
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }

        try {
            String query = "INSERT INTO value_metadata (id, value_type, unit, label, is_positive) VALUES (?, ?, ?, ?, ?)";
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                            dto.getId(),
                            dto.getType().name(),
                            dto.getUnit(),
                            dto.getLabel(),
                            dto.getIsPositive() != null ? dto.getIsPositive() : true
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Value metadata created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create value metadata.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating value metadata: ", e);
            return new ApiResponse<>(false, "Error creating value metadata: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<ValueMetadataDTO> read(String id) {
        String query = "SELECT * FROM value_metadata WHERE id = ?";
        try {
            List<ValueMetadataDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(ValueMetadataDTO.class)
                    .list();

            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "Value metadata found", dtoList.get(0));
            } else {
                String message = dtoList.isEmpty() ? "No value metadata found with the given ID." : "Multiple value metadata found with the same ID.";
                return new ApiResponse<>(false, message, null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading value metadata: ", e);
            return new ApiResponse<>(false, "Error reading value metadata: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<ValueMetadataDTO>> readAll() {
        String query = "SELECT * FROM value_metadata ORDER BY label";
        try {
            List<ValueMetadataDTO> dtoList = jdbcClient.sql(query).query(ValueMetadataDTO.class).list();
            return new ApiResponse<>(true, "All value metadata retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all value metadata: ", e);
            return new ApiResponse<>(false, "Error reading all value metadata: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<ValueMetadataDTO> update(ValueMetadataDTO dto) {
        try {
            String query = "UPDATE value_metadata SET value_type = ?, unit = ?, label = ?, is_positive = ? WHERE id = ?";
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                            dto.getType().name(),
                            dto.getUnit(),
                            dto.getLabel(),
                            dto.getIsPositive() != null ? dto.getIsPositive() : true,
                            dto.getId()
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Value metadata updated successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to update value metadata or metadata not found.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating value metadata: ", e);
            return new ApiResponse<>(false, "Error updating value metadata: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<ValueMetadataDTO> delete(String id) {
        try {
            ApiResponse<ValueMetadataDTO> readResponse = read(id);
            if (!readResponse.isSuccess()) {
                return new ApiResponse<>(false, "Value metadata not found for deletion", null);
            }

            String query = "DELETE FROM value_metadata WHERE id = ?";
            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Value metadata deleted successfully", readResponse.getData());
            } else {
                return new ApiResponse<>(false, "Failed to delete value metadata.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting value metadata: ", e);
            return new ApiResponse<>(false, "Error deleting value metadata: " + e.getMessage(), null);
        }
    }
}
