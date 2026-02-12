package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.legislation.LegislationAnnotationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class LegislationAnnotationDAO extends AbstractDAO<LegislationAnnotationDTO> {

    private static final Logger logger = Logger.getLogger(LegislationAnnotationDAO.class.getName());
    private final JdbcClient jdbcClient;

    @Autowired
    public LegislationAnnotationDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public ApiResponse<LegislationAnnotationDTO> create(LegislationAnnotationDTO dto) {
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }

        String query = "INSERT INTO legislation_annotations (id, legislation_id, user_id, group_id, annotation_type, body_json, target_json) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getId(),
                        dto.getLegislationId(),
                        dto.getUserId(),
                        dto.getGroupId() != null ? dto.getGroupId() : "",
                        dto.getAnnotationType() != null ? dto.getAnnotationType() : "",
                        dto.getBodyJson() != null ? dto.getBodyJson() : "",
                        dto.getTargetJson() != null ? dto.getTargetJson() : ""
                    ))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Annotation created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create annotation.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating annotation: ", e);
            return new ApiResponse<>(false, "Error creating annotation: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<LegislationAnnotationDTO> read(String id) {
        String query = "SELECT id, legislation_id AS legislationId, user_id AS userId, group_id AS groupId, " +
                        "annotation_type AS annotationType, body_json AS bodyJson, target_json AS targetJson, " +
                        "created_at AS createdAt, updated_at AS updatedAt " +
                        "FROM legislation_annotations WHERE id = ?";
        try {
            List<LegislationAnnotationDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(LegislationAnnotationDTO.class)
                    .list();
            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "Annotation found successfully", dtoList.get(0));
            } else {
                String message = dtoList.isEmpty() ? "No annotation found with the given ID." : "Multiple annotations found with the same ID.";
                return new ApiResponse<>(false, message, null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading annotation: ", e);
            return new ApiResponse<>(false, "Error reading annotation: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<LegislationAnnotationDTO>> readAll() {
        String query = "SELECT id, legislation_id AS legislationId, user_id AS userId, group_id AS groupId, " +
                        "annotation_type AS annotationType, body_json AS bodyJson, target_json AS targetJson, " +
                        "created_at AS createdAt, updated_at AS updatedAt " +
                        "FROM legislation_annotations ORDER BY created_at DESC";
        try {
            List<LegislationAnnotationDTO> dtoList = jdbcClient.sql(query).query(LegislationAnnotationDTO.class).list();
            return new ApiResponse<>(true, "All annotations retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all annotations: ", e);
            return new ApiResponse<>(false, "Error reading all annotations: " + e.getMessage(), null);
        }
    }

    public ApiResponse<List<LegislationAnnotationDTO>> readByLegislation(String legislationId) {
        String query = "SELECT id, legislation_id AS legislationId, user_id AS userId, group_id AS groupId, " +
                        "annotation_type AS annotationType, body_json AS bodyJson, target_json AS targetJson, " +
                        "created_at AS createdAt, updated_at AS updatedAt " +
                        "FROM legislation_annotations WHERE legislation_id = ? ORDER BY created_at DESC";
        try {
            List<LegislationAnnotationDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(legislationId))
                    .query(LegislationAnnotationDTO.class)
                    .list();
            return new ApiResponse<>(true, "Annotations retrieved for legislation", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading annotations by legislation: ", e);
            return new ApiResponse<>(false, "Error reading annotations by legislation: " + e.getMessage(), null);
        }
    }

    public ApiResponse<List<LegislationAnnotationDTO>> readByLegislationAndUser(String legislationId, String userId) {
        String query = "SELECT id, legislation_id AS legislationId, user_id AS userId, group_id AS groupId, " +
                        "annotation_type AS annotationType, body_json AS bodyJson, target_json AS targetJson, " +
                        "created_at AS createdAt, updated_at AS updatedAt " +
                        "FROM legislation_annotations WHERE legislation_id = ? AND user_id = ? ORDER BY created_at DESC";
        try {
            List<LegislationAnnotationDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(legislationId, userId))
                    .query(LegislationAnnotationDTO.class)
                    .list();
            return new ApiResponse<>(true, "Annotations retrieved for legislation and user", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading annotations by legislation and user: ", e);
            return new ApiResponse<>(false, "Error: " + e.getMessage(), null);
        }
    }

    public ApiResponse<List<LegislationAnnotationDTO>> readByLegislationAndGroup(String legislationId, String groupId) {
        String query = "SELECT id, legislation_id AS legislationId, user_id AS userId, group_id AS groupId, " +
                        "annotation_type AS annotationType, body_json AS bodyJson, target_json AS targetJson, " +
                        "created_at AS createdAt, updated_at AS updatedAt " +
                        "FROM legislation_annotations WHERE legislation_id = ? AND group_id = ? ORDER BY created_at DESC";
        try {
            List<LegislationAnnotationDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(legislationId, groupId))
                    .query(LegislationAnnotationDTO.class)
                    .list();
            return new ApiResponse<>(true, "Annotations retrieved for legislation and group", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading annotations by legislation and group: ", e);
            return new ApiResponse<>(false, "Error: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<LegislationAnnotationDTO> update(LegislationAnnotationDTO dto) {
        String query = "UPDATE legislation_annotations SET annotation_type = ?, body_json = ?, target_json = ?, group_id = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getAnnotationType() != null ? dto.getAnnotationType() : "",
                        dto.getBodyJson() != null ? dto.getBodyJson() : "",
                        dto.getTargetJson() != null ? dto.getTargetJson() : "",
                        dto.getGroupId() != null ? dto.getGroupId() : "",
                        dto.getId()
                    ))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Annotation updated successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to update annotation or annotation not found.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating annotation: ", e);
            return new ApiResponse<>(false, "Error updating annotation: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<LegislationAnnotationDTO> delete(String id) {
        String query = "DELETE FROM legislation_annotations WHERE id = ?";
        try {
            ApiResponse<LegislationAnnotationDTO> readResponse = read(id);
            if (!readResponse.isSuccess()) {
                return new ApiResponse<>(false, "Annotation not found for deletion", null);
            }

            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Annotation deleted successfully", readResponse.getData());
            } else {
                return new ApiResponse<>(false, "Failed to delete annotation.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting annotation: ", e);
            return new ApiResponse<>(false, "Error deleting annotation: " + e.getMessage(), null);
        }
    }
}
