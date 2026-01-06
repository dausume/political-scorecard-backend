package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.ContextualizedTermDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.TermContextDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.TermDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.ValueMetadataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for ContextualizedTerm.
 * Handles complex relationships with Term, TermContext, and ValueMetadata.
 */
@Repository
public class ContextualizedTermDAO extends AbstractDAO<ContextualizedTermDTO> {

    private static final Logger logger = Logger.getLogger(ContextualizedTermDAO.class.getName());
    private final JdbcClient jdbcClient;
    private final TermDAO termDAO;
    private final TermContextDAO contextDAO;
    private final ValueMetadataDAO metadataDAO;

    @Autowired
    public ContextualizedTermDAO(
            @Qualifier("scoringJdbcClient") JdbcClient jdbcClient,
            TermDAO termDAO,
            TermContextDAO contextDAO,
            ValueMetadataDAO metadataDAO) {
        this.jdbcClient = jdbcClient;
        this.termDAO = termDAO;
        this.contextDAO = contextDAO;
        this.metadataDAO = metadataDAO;
    }

    @Override
    public ApiResponse<ContextualizedTermDTO> create(ContextualizedTermDTO dto) {
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }

        try {
            // Insert main record
            String query = "INSERT INTO contextualized_term (id, term_id, value_metadata_id, pre_normalized_value, " +
                    "post_normalized_value, pre_process_polari_url, post_process_polari_url) VALUES (?, ?, ?, ?, ?, ?, ?)";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                            dto.getId(),
                            dto.getTermId(),
                            dto.getValueMetadataId(),
                            dto.getPreNormalizedValue() != null ? dto.getPreNormalizedValue() : 0.0,
                            dto.getPostNormalizedValue() != null ? dto.getPostNormalizedValue() : 0.0,
                            dto.getPreProcessPolariUrl(),
                            dto.getPostProcessPolariUrl()
                    ))
                    .update();

            if (rowsAffected > 0) {
                // Insert context relationships
                if (dto.getContextIds() != null && !dto.getContextIds().isEmpty()) {
                    insertContextRelationships(dto.getId(), dto.getContextIds());
                }

                // Fetch complete object for response
                return read(dto.getId());
            } else {
                return new ApiResponse<>(false, "Failed to create contextualized term.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating contextualized term: ", e);
            return new ApiResponse<>(false, "Error creating contextualized term: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<ContextualizedTermDTO> read(String id) {
        String query = "SELECT * FROM contextualized_term WHERE id = ?";
        try {
            List<ContextualizedTermBasicDTO> basicList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(ContextualizedTermBasicDTO.class)
                    .list();

            if (basicList.size() != 1) {
                String message = basicList.isEmpty() ? "No contextualized term found with the given ID." : "Multiple contextualized terms found with the same ID.";
                return new ApiResponse<>(false, message, null);
            }

            // Build complete DTO with related objects
            ContextualizedTermDTO dto = buildCompleteDTO(basicList.get(0));
            return new ApiResponse<>(true, "Contextualized term found", dto);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading contextualized term: ", e);
            return new ApiResponse<>(false, "Error reading contextualized term: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<ContextualizedTermDTO>> readAll() {
        String query = "SELECT * FROM contextualized_term ORDER BY created_at DESC";
        try {
            List<ContextualizedTermBasicDTO> basicList = jdbcClient.sql(query)
                    .query(ContextualizedTermBasicDTO.class)
                    .list();

            List<ContextualizedTermDTO> completeDTOs = new ArrayList<>();
            for (ContextualizedTermBasicDTO basic : basicList) {
                completeDTOs.add(buildCompleteDTO(basic));
            }

            return new ApiResponse<>(true, "All contextualized terms retrieved successfully", completeDTOs);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all contextualized terms: ", e);
            return new ApiResponse<>(false, "Error reading all contextualized terms: " + e.getMessage(), null);
        }
    }

    /**
     * Get all contextualized terms for a specific base term
     */
    public ApiResponse<List<ContextualizedTermDTO>> readByTermId(String termId) {
        String query = "SELECT * FROM contextualized_term WHERE term_id = ? ORDER BY created_at DESC";
        try {
            List<ContextualizedTermBasicDTO> basicList = jdbcClient.sql(query)
                    .params(List.of(termId))
                    .query(ContextualizedTermBasicDTO.class)
                    .list();

            List<ContextualizedTermDTO> completeDTOs = new ArrayList<>();
            for (ContextualizedTermBasicDTO basic : basicList) {
                completeDTOs.add(buildCompleteDTO(basic));
            }

            return new ApiResponse<>(true, "Contextualized terms for term ID retrieved", completeDTOs);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading contextualized terms by term ID: ", e);
            return new ApiResponse<>(false, "Error reading contextualized terms by term ID: " + e.getMessage(), null);
        }
    }

    /**
     * Get contextualized terms that match any of the provided context IDs.
     * This is used to filter terms based on selected primary, comparative, or critical contexts.
     *
     * @param contextIds List of context IDs to match against
     * @return ApiResponse containing matching contextualized terms
     */
    public ApiResponse<List<ContextualizedTermDTO>> readByContextIds(List<String> contextIds) {
        if (contextIds == null || contextIds.isEmpty()) {
            return new ApiResponse<>(true, "No context IDs provided, returning empty list", new ArrayList<>());
        }

        try {
            // Build query with IN clause for context IDs
            // We join with the junction table to find contextualized terms that have any of the specified contexts
            String placeholders = String.join(",", contextIds.stream().map(id -> "?").toArray(String[]::new));
            String query = "SELECT DISTINCT ct.* FROM contextualized_term ct " +
                          "INNER JOIN contextualized_term_contexts ctc ON ct.id = ctc.contextualized_term_id " +
                          "WHERE ctc.context_id IN (" + placeholders + ") " +
                          "ORDER BY ct.created_at DESC";

            List<ContextualizedTermBasicDTO> basicList = jdbcClient.sql(query)
                    .params(contextIds)
                    .query(ContextualizedTermBasicDTO.class)
                    .list();

            List<ContextualizedTermDTO> completeDTOs = new ArrayList<>();
            for (ContextualizedTermBasicDTO basic : basicList) {
                completeDTOs.add(buildCompleteDTO(basic));
            }

            return new ApiResponse<>(true, "Found " + completeDTOs.size() + " contextualized terms matching the provided contexts", completeDTOs);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading contextualized terms by context IDs: ", e);
            return new ApiResponse<>(false, "Error reading contextualized terms by context IDs: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<ContextualizedTermDTO> update(ContextualizedTermDTO dto) {
        try {
            String query = "UPDATE contextualized_term SET term_id = ?, value_metadata_id = ?, " +
                    "pre_normalized_value = ?, post_normalized_value = ?, pre_process_polari_url = ?, " +
                    "post_process_polari_url = ? WHERE id = ?";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                            dto.getTermId(),
                            dto.getValueMetadataId(),
                            dto.getPreNormalizedValue() != null ? dto.getPreNormalizedValue() : 0.0,
                            dto.getPostNormalizedValue() != null ? dto.getPostNormalizedValue() : 0.0,
                            dto.getPreProcessPolariUrl(),
                            dto.getPostProcessPolariUrl(),
                            dto.getId()
                    ))
                    .update();

            if (rowsAffected > 0) {
                // Update context relationships
                deleteContextRelationships(dto.getId());
                if (dto.getContextIds() != null && !dto.getContextIds().isEmpty()) {
                    insertContextRelationships(dto.getId(), dto.getContextIds());
                }

                return read(dto.getId());
            } else {
                return new ApiResponse<>(false, "Failed to update contextualized term or term not found.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating contextualized term: ", e);
            return new ApiResponse<>(false, "Error updating contextualized term: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<ContextualizedTermDTO> delete(String id) {
        try {
            ApiResponse<ContextualizedTermDTO> readResponse = read(id);
            if (!readResponse.isSuccess()) {
                return new ApiResponse<>(false, "Contextualized term not found for deletion", null);
            }

            // Delete context relationships (will cascade automatically)
            String query = "DELETE FROM contextualized_term WHERE id = ?";
            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Contextualized term deleted successfully", readResponse.getData());
            } else {
                return new ApiResponse<>(false, "Failed to delete contextualized term.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting contextualized term: ", e);
            return new ApiResponse<>(false, "Error deleting contextualized term: " + e.getMessage(), null);
        }
    }

    // Helper methods

    /**
     * Build a complete DTO with all related objects
     */
    private ContextualizedTermDTO buildCompleteDTO(ContextualizedTermBasicDTO basic) {
        ContextualizedTermDTO dto = new ContextualizedTermDTO();
        dto.setId(basic.getId());
        dto.setTermId(basic.getTermId());
        dto.setValueMetadataId(basic.getValueMetadataId());
        dto.setPreNormalizedValue(basic.getPreNormalizedValue());
        dto.setPostNormalizedValue(basic.getPostNormalizedValue());
        dto.setPreProcessPolariUrl(basic.getPreProcessPolariUrl());
        dto.setPostProcessPolariUrl(basic.getPostProcessPolariUrl());

        // Fetch related term
        if (basic.getTermId() != null) {
            ApiResponse<TermDTO> termResponse = termDAO.read(basic.getTermId());
            if (termResponse.isSuccess()) {
                dto.setTerm(termResponse.getData());
            }
        }

        // Fetch related value metadata
        if (basic.getValueMetadataId() != null) {
            ApiResponse<ValueMetadataDTO> metadataResponse = metadataDAO.read(basic.getValueMetadataId());
            if (metadataResponse.isSuccess()) {
                dto.setValueMetadata(metadataResponse.getData());
            }
        }

        // Fetch related contexts
        List<String> contextIds = getContextIds(basic.getId());
        dto.setContextIds(contextIds);

        List<TermContextDTO> contexts = new ArrayList<>();
        for (String contextId : contextIds) {
            ApiResponse<TermContextDTO> contextResponse = contextDAO.read(contextId);
            if (contextResponse.isSuccess()) {
                contexts.add(contextResponse.getData());
            }
        }
        dto.setContexts(contexts);

        return dto;
    }

    /**
     * Get context IDs for a contextualized term
     */
    private List<String> getContextIds(String contextualizedTermId) {
        String query = "SELECT context_id FROM contextualized_term_contexts WHERE contextualized_term_id = ?";
        try {
            return jdbcClient.sql(query)
                    .params(List.of(contextualizedTermId))
                    .query(String.class)
                    .list();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error fetching context IDs: ", e);
            return new ArrayList<>();
        }
    }

    /**
     * Insert context relationships
     */
    private void insertContextRelationships(String contextualizedTermId, List<String> contextIds) {
        String query = "INSERT INTO contextualized_term_contexts (contextualized_term_id, context_id) VALUES (?, ?)";
        for (String contextId : contextIds) {
            jdbcClient.sql(query).params(List.of(contextualizedTermId, contextId)).update();
        }
    }

    /**
     * Delete context relationships
     */
    private void deleteContextRelationships(String contextualizedTermId) {
        String query = "DELETE FROM contextualized_term_contexts WHERE contextualized_term_id = ?";
        jdbcClient.sql(query).params(List.of(contextualizedTermId)).update();
    }

    /**
     * Basic DTO for database queries
     */
    private static class ContextualizedTermBasicDTO {
        private String id;
        private String termId;
        private String valueMetadataId;
        private Double preNormalizedValue;
        private Double postNormalizedValue;
        private String preProcessPolariUrl;
        private String postProcessPolariUrl;

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTermId() {
            return termId;
        }

        public void setTermId(String termId) {
            this.termId = termId;
        }

        public String getValueMetadataId() {
            return valueMetadataId;
        }

        public void setValueMetadataId(String valueMetadataId) {
            this.valueMetadataId = valueMetadataId;
        }

        public Double getPreNormalizedValue() {
            return preNormalizedValue;
        }

        public void setPreNormalizedValue(Double preNormalizedValue) {
            this.preNormalizedValue = preNormalizedValue;
        }

        public Double getPostNormalizedValue() {
            return postNormalizedValue;
        }

        public void setPostNormalizedValue(Double postNormalizedValue) {
            this.postNormalizedValue = postNormalizedValue;
        }

        public String getPreProcessPolariUrl() {
            return preProcessPolariUrl;
        }

        public void setPreProcessPolariUrl(String preProcessPolariUrl) {
            this.preProcessPolariUrl = preProcessPolariUrl;
        }

        public String getPostProcessPolariUrl() {
            return postProcessPolariUrl;
        }

        public void setPostProcessPolariUrl(String postProcessPolariUrl) {
            this.postProcessPolariUrl = postProcessPolariUrl;
        }
    }
}
