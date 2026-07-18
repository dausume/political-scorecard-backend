package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.PolariInstanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class PolariInstanceDAO {

    private static final Logger logger = Logger.getLogger(PolariInstanceDAO.class.getName());
    private static final String SELECT = "SELECT id, name, base_url AS baseUrl, description, status, " +
            "created_at AS createdAt FROM polari_instance";
    private final JdbcClient jdbcClient;

    @Autowired
    public PolariInstanceDAO(@Qualifier("scoringJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public ApiResponse<PolariInstanceDTO> create(PolariInstanceDTO dto) {
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }
        if (dto.getStatus() == null || dto.getStatus().isEmpty()) {
            dto.setStatus("active");
        }
        String query = "INSERT INTO polari_instance (id, name, base_url, description, status) VALUES (?, ?, ?, ?, ?)";
        try {
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(
                        dto.getId(), dto.getName(), dto.getBaseUrl(),
                        dto.getDescription() != null ? dto.getDescription() : "",
                        dto.getStatus()))
                    .update();
            if (rowsAffected > 0) {
                return new ApiResponse<>(true, "Polari instance registered successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to register Polari instance.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error registering Polari instance: ", e);
            return new ApiResponse<>(false, "Error registering Polari instance: " + e.getMessage(), dto);
        }
    }

    public ApiResponse<List<PolariInstanceDTO>> readAll() {
        try {
            List<PolariInstanceDTO> dtoList = jdbcClient.sql(SELECT + " ORDER BY name")
                    .query(PolariInstanceDTO.class).list();
            return new ApiResponse<>(true, "All Polari instances retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading Polari instances: ", e);
            return new ApiResponse<>(false, "Error reading Polari instances: " + e.getMessage(), null);
        }
    }

    public ApiResponse<PolariInstanceDTO> readByName(String name) {
        try {
            List<PolariInstanceDTO> dtoList = jdbcClient.sql(SELECT + " WHERE name = ?")
                    .params(List.of(name))
                    .query(PolariInstanceDTO.class).list();
            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "Polari instance found", dtoList.get(0));
            }
            return new ApiResponse<>(false, "No Polari instance named '" + name + "'", null);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading Polari instance by name: ", e);
            return new ApiResponse<>(false, "Error reading Polari instance: " + e.getMessage(), null);
        }
    }
}
