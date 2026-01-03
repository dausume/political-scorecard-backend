package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.ContextHierarchyDTO;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Context Hierarchy operations.
 * Manages parent-child relationships between contexts.
 */
@Repository
public class ContextHierarchyDAO extends AbstractDAO<ContextHierarchyDTO> {

    private static final Logger logger = Logger.getLogger(ContextHierarchyDAO.class.getName());
    private final JdbcClient jdbcClient;

    @Autowired
    public ContextHierarchyDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public ApiResponse<ContextHierarchyDTO> create(ContextHierarchyDTO dto) {
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }

        try {
            String query = "INSERT INTO context_hierarchy (id, parent_context_id, child_context_id, context_type, specificity_difference, hierarchy_path) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                            dto.getId(),
                            dto.getParentContextId(),
                            dto.getChildContextId(),
                            dto.getContextType().name(),
                            dto.getSpecificityDifference(),
                            dto.getHierarchyPath()
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Context hierarchy created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create context hierarchy.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating context hierarchy: ", e);
            return new ApiResponse<>(false, "Error creating context hierarchy: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<ContextHierarchyDTO> read(String id) {
        String query = "SELECT * FROM context_hierarchy WHERE id = ?";
        try {
            List<ContextHierarchyDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(ContextHierarchyDTO.class)
                    .list();

            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "Context hierarchy found", dtoList.get(0));
            } else {
                String message = dtoList.isEmpty() ? "No context hierarchy found with the given ID." : "Multiple context hierarchies found with the same ID.";
                return new ApiResponse<>(false, message, null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading context hierarchy: ", e);
            return new ApiResponse<>(false, "Error reading context hierarchy: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<ContextHierarchyDTO>> readAll() {
        String query = "SELECT * FROM context_hierarchy ORDER BY context_type, hierarchy_path";
        try {
            List<ContextHierarchyDTO> dtoList = jdbcClient.sql(query).query(ContextHierarchyDTO.class).list();
            return new ApiResponse<>(true, "All context hierarchies retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all context hierarchies: ", e);
            return new ApiResponse<>(false, "Error reading all context hierarchies: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<ContextHierarchyDTO> update(ContextHierarchyDTO dto) {
        try {
            String query = "UPDATE context_hierarchy SET parent_context_id = ?, child_context_id = ?, " +
                    "context_type = ?, specificity_difference = ?, hierarchy_path = ? WHERE id = ?";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                            dto.getParentContextId(),
                            dto.getChildContextId(),
                            dto.getContextType().name(),
                            dto.getSpecificityDifference(),
                            dto.getHierarchyPath(),
                            dto.getId()
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Context hierarchy updated successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to update context hierarchy or hierarchy not found.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating context hierarchy: ", e);
            return new ApiResponse<>(false, "Error updating context hierarchy: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<ContextHierarchyDTO> delete(String id) {
        try {
            ApiResponse<ContextHierarchyDTO> readResponse = read(id);
            if (!readResponse.isSuccess()) {
                return new ApiResponse<>(false, "Context hierarchy not found for deletion", null);
            }

            String query = "DELETE FROM context_hierarchy WHERE id = ?";
            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Context hierarchy deleted successfully", readResponse.getData());
            } else {
                return new ApiResponse<>(false, "Failed to delete context hierarchy.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting context hierarchy: ", e);
            return new ApiResponse<>(false, "Error deleting context hierarchy: " + e.getMessage(), null);
        }
    }

    /**
     * Get all parent contexts for a given context (traversing upward in hierarchy)
     */
    public ApiResponse<List<ContextHierarchyDTO>> getParents(String childContextId) {
        String query = "SELECT * FROM context_hierarchy WHERE child_context_id = ? ORDER BY specificity_difference";
        try {
            List<ContextHierarchyDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(childContextId))
                    .query(ContextHierarchyDTO.class)
                    .list();
            return new ApiResponse<>(true, "Parent contexts retrieved", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting parent contexts: ", e);
            return new ApiResponse<>(false, "Error getting parent contexts: " + e.getMessage(), null);
        }
    }

    /**
     * Get all child contexts for a given context (traversing downward in hierarchy)
     */
    public ApiResponse<List<ContextHierarchyDTO>> getChildren(String parentContextId) {
        String query = "SELECT * FROM context_hierarchy WHERE parent_context_id = ? ORDER BY specificity_difference";
        try {
            List<ContextHierarchyDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(parentContextId))
                    .query(ContextHierarchyDTO.class)
                    .list();
            return new ApiResponse<>(true, "Child contexts retrieved", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting child contexts: ", e);
            return new ApiResponse<>(false, "Error getting child contexts: " + e.getMessage(), null);
        }
    }

    /**
     * Get all hierarchies for a specific context type
     */
    public ApiResponse<List<ContextHierarchyDTO>> getByType(TermContextType type) {
        String query = "SELECT * FROM context_hierarchy WHERE context_type = ? ORDER BY hierarchy_path";
        try {
            List<ContextHierarchyDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(type.name()))
                    .query(ContextHierarchyDTO.class)
                    .list();
            return new ApiResponse<>(true, "Context hierarchies for type " + type + " retrieved", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting hierarchies by type: ", e);
            return new ApiResponse<>(false, "Error getting hierarchies by type: " + e.getMessage(), null);
        }
    }
}
