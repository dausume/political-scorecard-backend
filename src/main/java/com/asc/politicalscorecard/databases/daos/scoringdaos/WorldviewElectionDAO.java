package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.WorldviewElectionDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for WorldviewElection.
 * Handles database operations for worldview elections in the scoring database.
 */
@Repository
public class WorldviewElectionDAO extends AbstractDAO<WorldviewElectionDTO> {

    private static final Logger logger = Logger.getLogger(WorldviewElectionDAO.class.getName());
    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public WorldviewElectionDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Maps a ResultSet row to a WorldviewElectionDTO
     */
    private WorldviewElectionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        WorldviewElectionDTO dto = new WorldviewElectionDTO();
        dto.setId(rs.getString("id"));
        dto.setName(rs.getString("name"));
        dto.setDescription(rs.getString("description"));

        // Parse JSON array for election_types
        String electionTypesJson = rs.getString("election_types");
        if (electionTypesJson != null && !electionTypesJson.isEmpty()) {
            try {
                List<String> electionTypes = objectMapper.readValue(electionTypesJson, new TypeReference<List<String>>(){});
                dto.setElectionTypes(electionTypes);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error parsing election_types JSON: " + electionTypesJson, e);
                dto.setElectionTypes(new ArrayList<>());
            }
        } else {
            dto.setElectionTypes(new ArrayList<>());
        }

        dto.setStatus(rs.getString("status"));

        // Handle nullable timestamps
        if (rs.getTimestamp("start_date") != null) {
            dto.setStartDate(rs.getTimestamp("start_date").toLocalDateTime());
        }
        if (rs.getTimestamp("end_date") != null) {
            dto.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());
        }

        dto.setCreatedBy(rs.getString("created_by"));
        dto.setTotalBallots(rs.getInt("total_ballots"));

        if (rs.getTimestamp("created_at") != null) {
            dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            dto.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return dto;
    }

    @Override
    public ApiResponse<WorldviewElectionDTO> create(WorldviewElectionDTO dto) {
        // Generate ID if not provided
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }

        try {
            // Convert electionTypes list to JSON
            String electionTypesJson = objectMapper.writeValueAsString(dto.getElectionTypes());

            String query = "INSERT INTO worldview_election " +
                          "(id, name, description, election_types, status, start_date, end_date, created_by, total_ballots) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getId(),
                        dto.getName(),
                        dto.getDescription(),
                        electionTypesJson,
                        dto.getStatus() != null ? dto.getStatus() : "DRAFT",
                        dto.getStartDate(),
                        dto.getEndDate(),
                        dto.getCreatedBy(),
                        dto.getTotalBallots() != null ? dto.getTotalBallots() : 0
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "WorldviewElection created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create WorldviewElection.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating WorldviewElection: ", e);
            return new ApiResponse<>(false, "Error creating WorldviewElection: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<WorldviewElectionDTO> read(String id) {
        String query = "SELECT * FROM worldview_election WHERE id = ?";
        try {
            List<WorldviewElectionDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(this::mapRow)
                    .list();

            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "WorldviewElection found successfully", dtoList.get(0));
            } else {
                String message = dtoList.isEmpty() ? "No WorldviewElection found with the given ID." : "Multiple elections found with the same ID.";
                logger.log(Level.SEVERE, message);
                return new ApiResponse<>(false, message, null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading WorldviewElection: ", e);
            return new ApiResponse<>(false, "Error reading WorldviewElection: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<WorldviewElectionDTO>> readAll() {
        String query = "SELECT * FROM worldview_election ORDER BY created_at DESC";
        try {
            List<WorldviewElectionDTO> dtoList = jdbcClient.sql(query)
                    .query(this::mapRow)
                    .list();
            return new ApiResponse<>(true, "All WorldviewElections retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all WorldviewElections: ", e);
            return new ApiResponse<>(false, "Error reading all WorldviewElections: " + e.getMessage(), null);
        }
    }

    /**
     * Get elections by status
     */
    public ApiResponse<List<WorldviewElectionDTO>> readByStatus(String status) {
        String query = "SELECT * FROM worldview_election WHERE status = ? ORDER BY created_at DESC";
        try {
            List<WorldviewElectionDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(status))
                    .query(this::mapRow)
                    .list();
            return new ApiResponse<>(true, "Elections retrieved successfully for status: " + status, dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading elections by status: ", e);
            return new ApiResponse<>(false, "Error reading elections by status: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<WorldviewElectionDTO> update(WorldviewElectionDTO dto) {
        try {
            // Convert electionTypes list to JSON
            String electionTypesJson = objectMapper.writeValueAsString(dto.getElectionTypes());

            String query = "UPDATE worldview_election SET name = ?, description = ?, election_types = ?, " +
                          "status = ?, start_date = ?, end_date = ?, created_by = ?, total_ballots = ? " +
                          "WHERE id = ?";

            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getName(),
                        dto.getDescription(),
                        electionTypesJson,
                        dto.getStatus(),
                        dto.getStartDate(),
                        dto.getEndDate(),
                        dto.getCreatedBy(),
                        dto.getTotalBallots(),
                        dto.getId()
                    ))
                    .update();

            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "WorldviewElection updated successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to update WorldviewElection or election not found.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating WorldviewElection: ", e);
            return new ApiResponse<>(false, "Error updating WorldviewElection: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<WorldviewElectionDTO> delete(String id) {
        String query = "DELETE FROM worldview_election WHERE id = ?";
        try {
            // First, read the election to return it in the response
            ApiResponse<WorldviewElectionDTO> readResponse = read(id);
            if (!readResponse.isSuccess()) {
                return new ApiResponse<>(false, "WorldviewElection not found for deletion", null);
            }

            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "WorldviewElection deleted successfully", readResponse.getData());
            } else {
                return new ApiResponse<>(false, "Failed to delete WorldviewElection.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting WorldviewElection: ", e);
            return new ApiResponse<>(false, "Error deleting WorldviewElection: " + e.getMessage(), null);
        }
    }
}
