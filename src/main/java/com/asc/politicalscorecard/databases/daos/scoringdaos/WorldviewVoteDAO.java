package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.WorldviewVoteDTO;
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
 * Data Access Object for WorldviewVote — one citizen's cast vote in
 * one PolariVoteTopic. One vote per (topic, voter) is enforced at the
 * DB level (UNIQUE KEY); recasting is an honest failure here, not a
 * silent overwrite — a real "change your vote" flow is a documented
 * follow-up, not built tonight.
 */
@Repository
public class WorldviewVoteDAO extends AbstractDAO<WorldviewVoteDTO> {

    private static final Logger logger = Logger.getLogger(WorldviewVoteDAO.class.getName());
    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public WorldviewVoteDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
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

    private WorldviewVoteDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        WorldviewVoteDTO dto = new WorldviewVoteDTO();
        dto.setId(rs.getString("id"));
        dto.setTopicId(rs.getString("topic_id"));
        dto.setVoterId(rs.getString("voter_id"));
        dto.setApprovals(parseJsonList(rs.getString("approvals_json")));
        dto.setSoleChoice(rs.getString("sole_choice"));
        dto.setRanking(parseJsonList(rs.getString("ranking_json")));
        if (rs.getTimestamp("cast_at") != null) {
            dto.setCastAt(rs.getTimestamp("cast_at").toLocalDateTime());
        }
        return dto;
    }

    @Override
    public ApiResponse<WorldviewVoteDTO> create(WorldviewVoteDTO dto) {
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }
        try {
            String approvalsJson = objectMapper.writeValueAsString(
                dto.getApprovals() != null ? dto.getApprovals() : new ArrayList<>());
            String rankingJson = objectMapper.writeValueAsString(
                dto.getRanking() != null ? dto.getRanking() : new ArrayList<>());

            String query = "INSERT INTO worldview_vote " +
                          "(id, topic_id, voter_id, approvals_json, sole_choice, ranking_json) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getId(),
                        dto.getTopicId(),
                        dto.getVoterId(),
                        approvalsJson,
                        dto.getSoleChoice() != null ? dto.getSoleChoice() : "",
                        rankingJson
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Vote cast successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to cast vote.", dto);
            }
        } catch (Exception e) {
            // Most common real cause: UNIQUE KEY (topic_id, voter_id) —
            // this voter already voted on this topic. Surfaced honestly
            // rather than silently overwriting their prior vote.
            logger.log(Level.SEVERE, "Error casting vote: ", e);
            return new ApiResponse<>(false, "Error casting vote (you may have already voted on this topic): " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<WorldviewVoteDTO> read(String id) {
        String query = "SELECT * FROM worldview_vote WHERE id = ?";
        try {
            List<WorldviewVoteDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(this::mapRow)
                    .list();
            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "Vote found successfully", dtoList.get(0));
            } else {
                return new ApiResponse<>(false, "No vote found with the given ID.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading vote: ", e);
            return new ApiResponse<>(false, "Error reading vote: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<WorldviewVoteDTO>> readAll() {
        String query = "SELECT * FROM worldview_vote ORDER BY cast_at DESC";
        try {
            List<WorldviewVoteDTO> dtoList = jdbcClient.sql(query)
                    .query(this::mapRow)
                    .list();
            return new ApiResponse<>(true, "All votes retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all votes: ", e);
            return new ApiResponse<>(false, "Error reading all votes: " + e.getMessage(), null);
        }
    }

    /**
     * Every vote cast for one topic — the input to both the PSC-side
     * raw-count preview and the Polari replay-and-apply sync.
     */
    public ApiResponse<List<WorldviewVoteDTO>> readByTopic(String topicId) {
        String query = "SELECT * FROM worldview_vote WHERE topic_id = ? ORDER BY cast_at ASC";
        try {
            List<WorldviewVoteDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(topicId))
                    .query(this::mapRow)
                    .list();
            return new ApiResponse<>(true, "Votes retrieved successfully for topic: " + topicId, dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading votes by topic: ", e);
            return new ApiResponse<>(false, "Error reading votes by topic: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<WorldviewVoteDTO> update(WorldviewVoteDTO dto) {
        try {
            String approvalsJson = objectMapper.writeValueAsString(
                dto.getApprovals() != null ? dto.getApprovals() : new ArrayList<>());
            String rankingJson = objectMapper.writeValueAsString(
                dto.getRanking() != null ? dto.getRanking() : new ArrayList<>());

            String query = "UPDATE worldview_vote SET approvals_json = ?, sole_choice = ?, " +
                          "ranking_json = ? WHERE id = ?";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        approvalsJson,
                        dto.getSoleChoice() != null ? dto.getSoleChoice() : "",
                        rankingJson,
                        dto.getId()
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Vote updated successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to update vote or vote not found.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating vote: ", e);
            return new ApiResponse<>(false, "Error updating vote: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<WorldviewVoteDTO> delete(String id) {
        String query = "DELETE FROM worldview_vote WHERE id = ?";
        try {
            ApiResponse<WorldviewVoteDTO> readResponse = read(id);
            if (!readResponse.isSuccess()) {
                return new ApiResponse<>(false, "Vote not found for deletion", null);
            }
            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Vote deleted successfully", readResponse.getData());
            } else {
                return new ApiResponse<>(false, "Failed to delete vote.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting vote: ", e);
            return new ApiResponse<>(false, "Error deleting vote: " + e.getMessage(), null);
        }
    }
}
