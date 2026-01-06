package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.DebateMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for DebateMessage.
 * Handles database operations for debate messages in the scoring database.
 */
@Repository
public class DebateMessageDAO extends AbstractDAO<DebateMessageDTO> {

    private static final Logger logger = Logger.getLogger(DebateMessageDAO.class.getName());
    private final JdbcClient jdbcClient;

    @Autowired
    public DebateMessageDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * Maps a ResultSet row to a DebateMessageDTO
     */
    private DebateMessageDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        DebateMessageDTO dto = new DebateMessageDTO();
        dto.setId(rs.getString("id"));
        dto.setElectionId(rs.getString("election_id"));
        dto.setUserId(rs.getString("user_id"));
        dto.setUsername(rs.getString("username"));
        dto.setMessage(rs.getString("message"));

        if (rs.getTimestamp("timestamp") != null) {
            dto.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        }

        dto.setEdited(rs.getBoolean("edited"));
        if (rs.getTimestamp("edited_at") != null) {
            dto.setEditedAt(rs.getTimestamp("edited_at").toLocalDateTime());
        }

        dto.setDeleted(rs.getBoolean("deleted"));
        if (rs.getTimestamp("deleted_at") != null) {
            dto.setDeletedAt(rs.getTimestamp("deleted_at").toLocalDateTime());
        }

        if (rs.getTimestamp("created_at") != null) {
            dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            dto.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return dto;
    }

    @Override
    public ApiResponse<DebateMessageDTO> create(DebateMessageDTO dto) {
        // Generate ID if not provided
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }

        try {
            String query = "INSERT INTO debate_message " +
                          "(id, election_id, user_id, username, message, timestamp, edited, deleted) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getId(),
                        dto.getElectionId(),
                        dto.getUserId(),
                        dto.getUsername(),
                        dto.getMessage(),
                        dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now(),
                        dto.getEdited() != null ? dto.getEdited() : false,
                        dto.getDeleted() != null ? dto.getDeleted() : false
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "DebateMessage created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create DebateMessage.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating DebateMessage: ", e);
            return new ApiResponse<>(false, "Error creating DebateMessage: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<DebateMessageDTO> read(String id) {
        String query = "SELECT * FROM debate_message WHERE id = ?";
        try {
            List<DebateMessageDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(this::mapRow)
                    .list();

            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "DebateMessage found successfully", dtoList.get(0));
            } else {
                String message = dtoList.isEmpty() ? "No DebateMessage found with the given ID." : "Multiple messages found with the same ID.";
                logger.log(Level.SEVERE, message);
                return new ApiResponse<>(false, message, null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading DebateMessage: ", e);
            return new ApiResponse<>(false, "Error reading DebateMessage: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<DebateMessageDTO>> readAll() {
        String query = "SELECT * FROM debate_message ORDER BY timestamp ASC";
        try {
            List<DebateMessageDTO> dtoList = jdbcClient.sql(query)
                    .query(this::mapRow)
                    .list();
            return new ApiResponse<>(true, "All DebateMessages retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all DebateMessages: ", e);
            return new ApiResponse<>(false, "Error reading all DebateMessages: " + e.getMessage(), null);
        }
    }

    /**
     * Get all messages for a specific election, ordered by timestamp
     * Excludes soft-deleted messages
     */
    public ApiResponse<List<DebateMessageDTO>> readByElectionId(String electionId) {
        String query = "SELECT * FROM debate_message WHERE election_id = ? AND deleted = FALSE ORDER BY timestamp ASC";
        try {
            List<DebateMessageDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(electionId))
                    .query(this::mapRow)
                    .list();
            return new ApiResponse<>(true, "Messages retrieved successfully for election: " + electionId, dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading messages by election ID: ", e);
            return new ApiResponse<>(false, "Error reading messages by election ID: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<DebateMessageDTO> update(DebateMessageDTO dto) {
        try {
            String query = "UPDATE debate_message SET message = ?, edited = ?, edited_at = ? WHERE id = ?";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getMessage(),
                        true,
                        LocalDateTime.now(),
                        dto.getId()
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "DebateMessage updated successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to update DebateMessage or message not found.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating DebateMessage: ", e);
            return new ApiResponse<>(false, "Error updating DebateMessage: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<DebateMessageDTO> delete(String id) {
        // Soft delete - set deleted flag instead of removing from database
        String query = "UPDATE debate_message SET deleted = ?, deleted_at = ? WHERE id = ?";
        try {
            ApiResponse<DebateMessageDTO> readResponse = read(id);
            if (!readResponse.isSuccess()) {
                return new ApiResponse<>(false, "DebateMessage not found for deletion", null);
            }

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(true, LocalDateTime.now(), id))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "DebateMessage deleted successfully", readResponse.getData());
            } else {
                return new ApiResponse<>(false, "Failed to delete DebateMessage.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting DebateMessage: ", e);
            return new ApiResponse<>(false, "Error deleting DebateMessage: " + e.getMessage(), null);
        }
    }
}
