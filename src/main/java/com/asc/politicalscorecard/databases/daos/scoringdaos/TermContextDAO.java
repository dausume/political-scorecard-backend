package com.asc.politicalscorecard.databases.daos.scoringdaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.AbstractDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.*;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContext;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for TermContext.
 * Uses MariaDB for basic metadata and Redis for full polymorphic context objects.
 */
@Repository
public class TermContextDAO extends AbstractDAO<TermContextDTO> {

    private static final Logger logger = Logger.getLogger(TermContextDAO.class.getName());
    private final JdbcClient jdbcClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String REDIS_KEY_PREFIX = "scoringcache:term_contexts";

    @Autowired
    public TermContextDAO(
            @Qualifier("scoringJdbcClient") JdbcClient jdbcClient,
            @Qualifier("scoringCacheRedisClient") RedisTemplate<String, String> redisTemplate) {
        this.jdbcClient = jdbcClient;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ApiResponse<TermContextDTO> create(TermContextDTO dto) {
        // Generate ID if not provided
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId();
        }

        try {
            // Store basic metadata in MariaDB
            String query = "INSERT INTO term_context (id, type, label) VALUES (?, ?, ?)";
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(dto.getId(), dto.getType().name(), dto.getLabel()))
                    .update();

            if (rowsAffected > 0) {
                // Store full polymorphic object in Redis
                String redisKey = REDIS_KEY_PREFIX;
                String hashKey = "context:" + dto.getId();
                String jsonValue = objectMapper.writeValueAsString(dto);
                redisTemplate.opsForHash().put(redisKey, hashKey, jsonValue);

                return new ApiResponse<>(true, "Term context created successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to create term context.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating term context: ", e);
            return new ApiResponse<>(false, "Error creating term context: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<TermContextDTO> read(String id) {
        try {
            // Try to get from Redis first (full object)
            String redisKey = REDIS_KEY_PREFIX;
            String hashKey = "context:" + id;
            Object cachedValue = redisTemplate.opsForHash().get(redisKey, hashKey);

            if (cachedValue != null) {
                // Deserialize from Redis
                TermContextDTO dto = objectMapper.readValue(cachedValue.toString(), TermContextDTO.class);
                return new ApiResponse<>(true, "Term context found in cache", dto);
            }

            // Fallback to MariaDB (basic metadata only)
            String query = "SELECT * FROM term_context WHERE id = ?";
            List<TermContextBasicDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(id))
                    .query(TermContextBasicDTO.class)
                    .list();

            if (dtoList.size() == 1) {
                return new ApiResponse<>(true, "Term context found (basic metadata only)", (TermContextDTO) dtoList.get(0));
            } else {
                String message = dtoList.isEmpty() ? "No term context found with the given ID." : "Multiple term contexts found with the same ID.";
                return new ApiResponse<>(false, message, null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading term context: ", e);
            return new ApiResponse<>(false, "Error reading term context: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<TermContextDTO>> readAll() {
        String query = "SELECT * FROM term_context ORDER BY type, label";
        try {
            List<TermContextBasicDTO> dtoList = jdbcClient.sql(query).query(TermContextBasicDTO.class).list();
            return new ApiResponse<>(true, "All term contexts retrieved successfully (basic metadata)", (List) dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all term contexts: ", e);
            return new ApiResponse<>(false, "Error reading all term contexts: " + e.getMessage(), null);
        }
    }

    /**
     * Get contexts by type
     */
    public ApiResponse<List<TermContextDTO>> readByType(TermContextType type) {
        String query = "SELECT * FROM term_context WHERE type = ? ORDER BY label";
        try {
            List<TermContextBasicDTO> dtoList = jdbcClient.sql(query)
                    .params(List.of(type.name()))
                    .query(TermContextBasicDTO.class)
                    .list();
            return new ApiResponse<>(true, "Term contexts retrieved for type: " + type, (List) dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading term contexts by type: ", e);
            return new ApiResponse<>(false, "Error reading term contexts by type: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<TermContextDTO> update(TermContextDTO dto) {
        try {
            // Update MariaDB
            String query = "UPDATE term_context SET type = ?, label = ? WHERE id = ?";
            int rowsAffected = jdbcClient.sql(query)
                    .params(List.of(dto.getType().name(), dto.getLabel(), dto.getId()))
                    .update();

            if (rowsAffected > 0) {
                // Update Redis
                String redisKey = REDIS_KEY_PREFIX;
                String hashKey = "context:" + dto.getId();
                String jsonValue = objectMapper.writeValueAsString(dto);
                redisTemplate.opsForHash().put(redisKey, hashKey, jsonValue);

                return new ApiResponse<>(true, "Term context updated successfully", dto);
            } else {
                return new ApiResponse<>(false, "Failed to update term context or context not found.", dto);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating term context: ", e);
            return new ApiResponse<>(false, "Error updating term context: " + e.getMessage(), dto);
        }
    }

    @Override
    public ApiResponse<TermContextDTO> delete(String id) {
        try {
            // First, read the context to return it
            ApiResponse<TermContextDTO> readResponse = read(id);
            if (!readResponse.isSuccess()) {
                return new ApiResponse<>(false, "Term context not found for deletion", null);
            }

            // Delete from MariaDB
            String query = "DELETE FROM term_context WHERE id = ?";
            int rowsAffected = jdbcClient.sql(query).params(List.of(id)).update();

            if (rowsAffected > 0) {
                // Delete from Redis
                String redisKey = REDIS_KEY_PREFIX;
                String hashKey = "context:" + id;
                redisTemplate.opsForHash().delete(redisKey, hashKey);

                return new ApiResponse<>(true, "Term context deleted successfully", readResponse.getData());
            } else {
                return new ApiResponse<>(false, "Failed to delete term context.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting term context: ", e);
            return new ApiResponse<>(false, "Error deleting term context: " + e.getMessage(), null);
        }
    }

    /**
     * Basic DTO for MariaDB queries (only has id, type, label)
     */
    private static class TermContextBasicDTO extends TermContextDTO {
        @Override
        public String getValue() {
            return getLabel();
        }

        @Override
        public TermContext toEntity() {
            return null;
        }
    }
}
