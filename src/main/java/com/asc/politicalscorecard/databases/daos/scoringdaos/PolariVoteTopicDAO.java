package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.PolariVoteTopicDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for PolariVoteTopic — a PSC-hosted public vote
 * over candidate definitions drafted in Polari.
 */
@Repository
public class PolariVoteTopicDAO extends AbstractDAO<PolariVoteTopicDTO> {

    private static final Logger logger = Logger.getLogger(PolariVoteTopicDAO.class.getName());
    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public PolariVoteTopicDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = new ObjectMapper();
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing JSON list: " + json, e);
            return new ArrayList<>();
        }
    }

    private PolariVoteTopicDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        PolariVoteTopicDTO dto = new PolariVoteTopicDTO();
        dto.setId(rs.getString("id"));
        dto.setTitle(rs.getString("title"));
        dto.setDescription(rs.getString("description"));
        dto.setPolariGroupName(rs.getString("polari_group_name"));
        dto.setPolariItemKind(rs.getString("polari_item_kind"));
        dto.setCandidateNames(parseJsonList(rs.getString("candidate_names_json")));
        dto.setMode(rs.getString("mode"));
        dto.setStatus(rs.getString("status"));
        dto.setCreatedBy(rs.getString("created_by"));
        dto.setTotalBallots(rs.getInt("total_ballots"));
        dto.setElectedCandidate(rs.getString("elected_candidate"));
        dto.setElectedProvenance(rs.getString("elected_provenance"));
        if (rs.getTimestamp("created_at") != null) {
            dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            dto.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return dto;
    }

    @Override
    public ApiResponse<PolariVoteTopicDTO> create(PolariVoteTopicDTO dto) {
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }
        try {
            // Resolve effective defaults BEFORE inserting, and write
            // them back onto the dto — otherwise a create() response
            // shows null for fields the DB actually defaulted, which
            // is a real correctness bug, not just cosmetic.
            if (dto.getMode() == null) {
                dto.setMode("approval");
            }
            if (dto.getStatus() == null) {
                dto.setStatus("OPEN");
            }
            if (dto.getDescription() == null) {
                dto.setDescription("");
            }
            if (dto.getCreatedBy() == null) {
                dto.setCreatedBy("");
            }
            if (dto.getTotalBallots() == null) {
                dto.setTotalBallots(0);
            }

            String candidatesJson = objectMapper.writeValueAsString(dto.getCandidateNames());
            String query = "INSERT INTO polari_vote_topic " +
                          "(id, title, description, polari_group_name, polari_item_kind, " +
                          "candidate_names_json, mode, status, created_by, total_ballots) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getId(),
                        dto.getTitle(),
                        dto.getDescription(),
                        dto.getPolariGroupName(),
                        dto.getPolariItemKind(),
                        candidatesJson,
                        dto.getMode(),
                        dto.getStatus(),
                        dto.getCreatedBy(),
                        dto.getTotalBallots()
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "PolariVoteTopic created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create PolariVoteTopic.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating PolariVoteTopic: ", e);
            return new ApiResponse<>(false, "Error creating PolariVoteTopic: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<PolariVoteTopicDTO> read(String id) {
        String query = "SELECT * FROM polari_vote_topic WHERE id = ?";
        try {
            List<PolariVoteTopicDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(this::mapRow)
                    .list();

            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "PolariVoteTopic found successfully", dtoList.get(0));
            } else {
                String message = dtoList.isEmpty() ? "No PolariVoteTopic found with the given ID." : "Multiple topics found with the same ID.";
                logger.log(Level.SEVERE, message);
                return new ApiResponse<>(false, message, null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading PolariVoteTopic: ", e);
            return new ApiResponse<>(false, "Error reading PolariVoteTopic: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<PolariVoteTopicDTO>> readAll() {
        String query = "SELECT * FROM polari_vote_topic ORDER BY created_at DESC";
        try {
            List<PolariVoteTopicDTO> dtoList = jdbcClient.sql(query)
                    .query(this::mapRow)
                    .list();
            return new ApiResponse<>(true, "All PolariVoteTopics retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all PolariVoteTopics: ", e);
            return new ApiResponse<>(false, "Error reading all PolariVoteTopics: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<PolariVoteTopicDTO> update(PolariVoteTopicDTO dto) {
        try {
            String candidatesJson = objectMapper.writeValueAsString(dto.getCandidateNames());
            String query = "UPDATE polari_vote_topic SET title = ?, description = ?, " +
                          "polari_group_name = ?, polari_item_kind = ?, candidate_names_json = ?, " +
                          "mode = ?, status = ?, total_ballots = ?, elected_candidate = ?, " +
                          "elected_provenance = ? WHERE id = ?";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getTitle(),
                        dto.getDescription() != null ? dto.getDescription() : "",
                        dto.getPolariGroupName(),
                        dto.getPolariItemKind(),
                        candidatesJson,
                        dto.getMode(),
                        dto.getStatus(),
                        dto.getTotalBallots() != null ? dto.getTotalBallots() : 0,
                        dto.getElectedCandidate() != null ? dto.getElectedCandidate() : "",
                        dto.getElectedProvenance() != null ? dto.getElectedProvenance() : "",
                        dto.getId()
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "PolariVoteTopic updated successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to update PolariVoteTopic or topic not found.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating PolariVoteTopic: ", e);
            return new ApiResponse<>(false, "Error updating PolariVoteTopic: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<PolariVoteTopicDTO> delete(String id) {
        String query = "DELETE FROM polari_vote_topic WHERE id = ?";
        try {
            ApiResponse<PolariVoteTopicDTO> readResponse = read(id);
            if (!readResponse.isSuccess()) {
                return new ApiResponse<>(false, "PolariVoteTopic not found for deletion", null);
            }

            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "PolariVoteTopic deleted successfully", readResponse.getData());
            } else {
                return new ApiResponse<>(false, "Failed to delete PolariVoteTopic.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting PolariVoteTopic: ", e);
            return new ApiResponse<>(false, "Error deleting PolariVoteTopic: " + e.getMessage(), null);
        }
    }

    /**
     * Increment total_ballots by one — called after a vote is cast so
     * readAll()/read() reflect the count without a separate COUNT
     * query on every list render.
     */
    public void incrementTotalBallots(String id) {
        try {
            jdbcClient.sql("UPDATE polari_vote_topic SET total_ballots = total_ballots + 1 WHERE id = ?")
                    .params(List.of(id))
                    .update();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error incrementing total_ballots: ", e);
        }
    }
}
