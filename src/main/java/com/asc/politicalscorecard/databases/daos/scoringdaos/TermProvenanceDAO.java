package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.TermProvenanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class TermProvenanceDAO {

    private static final Logger logger = Logger.getLogger(TermProvenanceDAO.class.getName());
    private static final String SELECT = "SELECT id, term_id AS termId, term_name AS termName, " +
            "context_name AS contextName, group_name AS groupName, instance_name AS instanceName, " +
            "signal_name AS signalName, requested_by AS requestedBy, verdict_json AS verdictJson, " +
            "admitted_at AS admittedAt FROM term_provenance";
    private final JdbcClient jdbcClient;

    @Autowired
    public TermProvenanceDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public ApiResponse<TermProvenanceDTO> create(TermProvenanceDTO dto) {
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }
        String query = "INSERT INTO term_provenance (id, term_id, term_name, context_name, group_name, " +
                "instance_name, signal_name, requested_by, verdict_json) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getId(),
                        dto.getTermId() != null ? dto.getTermId() : "",
                        dto.getTermName(),
                        dto.getContextName() != null ? dto.getContextName() : "",
                        dto.getGroupName(), dto.getInstanceName(),
                        dto.getSignalName() != null ? dto.getSignalName() : "",
                        dto.getRequestedBy() != null ? dto.getRequestedBy() : "",
                        dto.getVerdictJson() != null ? dto.getVerdictJson() : "{}"))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Term provenance recorded successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to record term provenance.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error recording term provenance: ", e);
            return new ApiResponse<>(false, "Error recording term provenance: " + e.getMessage(), dto);
        }
    }

    public ApiResponse<List<TermProvenanceDTO>> readAll() {
        try {
            List<TermProvenanceDTO> dtoList = jdbcClient.sql(SELECT + " ORDER BY admitted_at DESC")
                    .query(TermProvenanceDTO.class).list();
            return new ApiResponse<>(true, "All term provenance retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading term provenance: ", e);
            return new ApiResponse<>(false, "Error reading term provenance: " + e.getMessage(), null);
        }
    }

    public ApiResponse<List<TermProvenanceDTO>> readByTermId(String termId) {
        try {
            List<TermProvenanceDTO> dtoList = jdbcClient.sql(SELECT + " WHERE term_id = ?")
                    .params(List.of(termId))
                    .query(TermProvenanceDTO.class).list();
            return new ApiResponse<>(true, "Term provenance retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading term provenance by term: ", e);
            return new ApiResponse<>(false, "Error reading term provenance: " + e.getMessage(), null);
        }
    }
}
