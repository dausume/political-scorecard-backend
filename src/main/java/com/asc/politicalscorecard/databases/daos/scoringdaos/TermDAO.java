package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.TermDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Term.
 * Handles database operations for terms in the scoring database.
 */
@Repository
public class TermDAO extends AbstractDAO<TermDTO> {

    private static final Logger logger = Logger.getLogger(TermDAO.class.getName());
    private final JdbcClient jdbcClient;

    @Autowired
    public TermDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public ApiResponse<TermDTO> create(TermDTO dto) {
        // Generate ID if not provided
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }

        String query = "INSERT INTO term (id, name, description, source, category) VALUES (?, ?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(dto.getId(), dto.getName(), dto.getDescription(), dto.getSource(), dto.getCategory()))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Term created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create term.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating term: ", e);
            return new ApiResponse<>(false, "Error creating term: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<TermDTO> read(String id) {
        String query = "SELECT * FROM term WHERE id = ?";
        try {
            List<TermDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(TermDTO.class)
                    .list();
            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "Term found successfully", dtoList.get(0));
            } else {
                String message = dtoList.isEmpty() ? "No term found with the given ID." : "Multiple terms found with the same ID.";
                logger.log(Level.SEVERE, message);
                return new ApiResponse<>(false, message, null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading term: ", e);
            return new ApiResponse<>(false, "Error reading term: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<TermDTO>> readAll() {
        String query = "SELECT * FROM term ORDER BY name";
        try {
            List<TermDTO> dtoList = jdbcClient.sql(query).query(TermDTO.class).list();
            return new ApiResponse<>(true, "All terms retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all terms: ", e);
            return new ApiResponse<>(false, "Error reading all terms: " + e.getMessage(), null);
        }
    }

    /**
     * Get terms by category
     */
    public ApiResponse<List<TermDTO>> readByCategory(String category) {
        String query = "SELECT * FROM term WHERE category = ? ORDER BY name";
        try {
            List<TermDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(category))
                    .query(TermDTO.class)
                    .list();
            return new ApiResponse<>(true, "Terms retrieved successfully for category: " + category, dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading terms by category: ", e);
            return new ApiResponse<>(false, "Error reading terms by category: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<TermDTO> update(TermDTO dto) {
        String query = "UPDATE term SET name = ?, description = ?, source = ?, category = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(dto.getName(), dto.getDescription(), dto.getSource(), dto.getCategory(), dto.getId()))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Term updated successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to update term or term not found.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating term: ", e);
            return new ApiResponse<>(false, "Error updating term: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<TermDTO> delete(String id) {
        String query = "DELETE FROM term WHERE id = ?";
        try {
            // First, read the term to return it in the response
            ApiResponse<TermDTO> readResponse = read(id);
            if (!readResponse.isSuccess()) {
                return new ApiResponse<>(false, "Term not found for deletion", null);
            }

            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Term deleted successfully", readResponse.getData());
            } else {
                return new ApiResponse<>(false, "Failed to delete term.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting term: ", e);
            return new ApiResponse<>(false, "Error deleting term: " + e.getMessage(), null);
        }
    }
}
