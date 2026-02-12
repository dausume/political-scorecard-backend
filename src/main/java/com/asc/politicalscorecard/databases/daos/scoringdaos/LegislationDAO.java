package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.legislation.LegislationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class LegislationDAO extends AbstractDAO<LegislationDTO> {

    private static final Logger logger = Logger.getLogger(LegislationDAO.class.getName());
    private final JdbcClient jdbcClient;

    @Autowired
    public LegislationDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public ApiResponse<LegislationDTO> create(LegislationDTO dto) {
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            dto.setStatus("DRAFT");
        }

        String query = "INSERT INTO legislation (id, title, description, legislative_body_id, valid_from_date, valid_to_date, legislation_text, url, status, created_by) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getId(), dto.getTitle(), dto.getDescription(),
                        dto.getLegislativeBodyId() != null ? dto.getLegislativeBodyId() : "",
                        dto.getValidFromDate() != null ? dto.getValidFromDate() : "",
                        dto.getValidToDate() != null ? dto.getValidToDate() : "",
                        dto.getLegislationText() != null ? dto.getLegislationText() : "",
                        dto.getUrl() != null ? dto.getUrl() : "",
                        dto.getStatus(),
                        dto.getCreatedBy() != null ? dto.getCreatedBy() : ""
                    ))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Legislation created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create legislation.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating legislation: ", e);
            return new ApiResponse<>(false, "Error creating legislation: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<LegislationDTO> read(String id) {
        String query = "SELECT id, title, description, legislative_body_id AS legislativeBodyId, " +
                        "valid_from_date AS validFromDate, valid_to_date AS validToDate, " +
                        "legislation_text AS legislationText, url, status, " +
                        "created_by AS createdBy, created_at AS createdAt, updated_at AS updatedAt " +
                        "FROM legislation WHERE id = ?";
        try {
            List<LegislationDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(LegislationDTO.class)
                    .list();
            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "Legislation found successfully", dtoList.get(0));
            } else {
                String message = dtoList.isEmpty() ? "No legislation found with the given ID." : "Multiple legislations found with the same ID.";
                logger.log(Level.SEVERE, message);
                return new ApiResponse<>(false, message, null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading legislation: ", e);
            return new ApiResponse<>(false, "Error reading legislation: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<LegislationDTO>> readAll() {
        String query = "SELECT id, title, description, legislative_body_id AS legislativeBodyId, " +
                        "valid_from_date AS validFromDate, valid_to_date AS validToDate, " +
                        "legislation_text AS legislationText, url, status, " +
                        "created_by AS createdBy, created_at AS createdAt, updated_at AS updatedAt " +
                        "FROM legislation ORDER BY updated_at DESC";
        try {
            List<LegislationDTO> dtoList = jdbcClient.sql(query).query(LegislationDTO.class).list();
            return new ApiResponse<>(true, "All legislations retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all legislations: ", e);
            return new ApiResponse<>(false, "Error reading all legislations: " + e.getMessage(), null);
        }
    }

    public ApiResponse<List<LegislationDTO>> readByStatus(String status) {
        String query = "SELECT id, title, description, legislative_body_id AS legislativeBodyId, " +
                        "valid_from_date AS validFromDate, valid_to_date AS validToDate, " +
                        "legislation_text AS legislationText, url, status, " +
                        "created_by AS createdBy, created_at AS createdAt, updated_at AS updatedAt " +
                        "FROM legislation WHERE status = ? ORDER BY updated_at DESC";
        try {
            List<LegislationDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(status))
                    .query(LegislationDTO.class)
                    .list();
            return new ApiResponse<>(true, "Legislations retrieved successfully for status: " + status, dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading legislations by status: ", e);
            return new ApiResponse<>(false, "Error reading legislations by status: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<LegislationDTO> update(LegislationDTO dto) {
        String query = "UPDATE legislation SET title = ?, description = ?, legislative_body_id = ?, " +
                        "valid_from_date = ?, valid_to_date = ?, legislation_text = ?, url = ?, status = ? " +
                        "WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getTitle(), dto.getDescription(),
                        dto.getLegislativeBodyId() != null ? dto.getLegislativeBodyId() : "",
                        dto.getValidFromDate() != null ? dto.getValidFromDate() : "",
                        dto.getValidToDate() != null ? dto.getValidToDate() : "",
                        dto.getLegislationText() != null ? dto.getLegislationText() : "",
                        dto.getUrl() != null ? dto.getUrl() : "",
                        dto.getStatus() != null ? dto.getStatus() : "DRAFT",
                        dto.getId()
                    ))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Legislation updated successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to update legislation or legislation not found.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating legislation: ", e);
            return new ApiResponse<>(false, "Error updating legislation: " + e.getMessage(), dto);
        }
    }

    public ApiResponse<LegislationDTO> updateStatus(String id, String status) {
        String query = "UPDATE legislation SET status = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(status, id))
                    .update();
            if (rowsAffected > 0) {
                return read(id);
            } else {
                return new ApiResponse<>(false, "Failed to update legislation status.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating legislation status: ", e);
            return new ApiResponse<>(false, "Error updating legislation status: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<LegislationDTO> delete(String id) {
        String query = "DELETE FROM legislation WHERE id = ?";
        try {
            ApiResponse<LegislationDTO> readResponse = read(id);
            if (!readResponse.isSuccess()) {
                return new ApiResponse<>(false, "Legislation not found for deletion", null);
            }

            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Legislation deleted successfully", readResponse.getData());
            } else {
                return new ApiResponse<>(false, "Failed to delete legislation.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting legislation: ", e);
            return new ApiResponse<>(false, "Error deleting legislation: " + e.getMessage(), null);
        }
    }
}
