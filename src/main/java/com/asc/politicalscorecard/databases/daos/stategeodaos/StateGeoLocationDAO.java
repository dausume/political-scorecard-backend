package com.asc.politicalscorecard.databases.daos.stategeodaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.statedto.stategeolocationdto.StateGeoLocationDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Repository
public class StateGeoLocationDAO {

    private static final Logger logger = Logger.getLogger(StateGeoLocationDAO.class.getName());
    private final RedisTemplate<String, String> redisTemplate;
    private final String namespace = "geolocation:states";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public StateGeoLocationDAO(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // CREATE
    public ApiResponse<StateGeoLocationDTO> create(StateGeoLocationDTO dto) {
        try {
            redisTemplate.opsForHash().put(namespace, dto.getId(), dto.getGeoJson());
            return new ApiResponse<StateGeoLocationDTO>(true, "State GeoLocation created successfully", dto);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating state geolocation: ", e);
            return new ApiResponse<StateGeoLocationDTO>(false, "Error creating state geolocation: " + e.getMessage(), dto);
        }
    }

    // READ by ID
    public ApiResponse<StateGeoLocationDTO> read(String id) {
        try {
            String geoJsonData = (String) redisTemplate.opsForHash().get(namespace, id);
            if (geoJsonData != null) {
                StateGeoLocationDTO dto = new StateGeoLocationDTO(id, geoJsonData);
                return new ApiResponse<StateGeoLocationDTO>(true, "State GeoLocation found successfully", dto);
            } else {
                return new ApiResponse<StateGeoLocationDTO>(false, "No state geolocation found with the given ID.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading state geolocation: ", e);
            return new ApiResponse<StateGeoLocationDTO>(false, "Error reading state geolocation: " + e.getMessage(), null);
        }
    }

    // READ ALL
    public ApiResponse<List<StateGeoLocationDTO>> readAll() {
        try {
            Map<Object, Object> allGeoJsonData = redisTemplate.opsForHash().entries(namespace);
            List<StateGeoLocationDTO> dtoList = allGeoJsonData.entrySet().stream()
                .map(entry -> new StateGeoLocationDTO(entry.getKey().toString(), entry.getValue().toString()))
                .collect(Collectors.toList());
            return new ApiResponse<List<StateGeoLocationDTO>>(true, "All state geolocations retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all state geolocations: ", e);
            return new ApiResponse<List<StateGeoLocationDTO>>(false, "Error reading all state geolocations: " + e.getMessage(), null);
        }
    }

    // UPDATE
    public ApiResponse<StateGeoLocationDTO> update(StateGeoLocationDTO dto) {
        try {
            redisTemplate.opsForHash().put(namespace, dto.getId(), dto.getGeoJson());
            return new ApiResponse<StateGeoLocationDTO>(true, "State GeoLocation updated successfully", dto);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating state geolocation: ", e);
            return new ApiResponse<StateGeoLocationDTO>(false, "Error updating state geolocation: " + e.getMessage(), dto);
        }
    }

    // DELETE
    public ApiResponse<StateGeoLocationDTO> delete(String id) {
        try {
            Long rowsDeleted = redisTemplate.opsForHash().delete(namespace, id);
            if (rowsDeleted > 0) {
                return new ApiResponse<StateGeoLocationDTO>(true, "State GeoLocation deleted successfully", null);
            } else {
                return new ApiResponse<StateGeoLocationDTO>(false, "Failed to delete state geolocation.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting state geolocation: ", e);
            return new ApiResponse<StateGeoLocationDTO>(false, "Error deleting state geolocation: " + e.getMessage(), null);
        }
    }

    // READ Properties Only
    public ApiResponse<JsonNode> readProperties(String id) {
        try {
            String geoJsonData = (String) redisTemplate.opsForHash().get(namespace, id);
            if (geoJsonData != null) {
                JsonNode geoJsonNode = objectMapper.readTree(geoJsonData);
                JsonNode propertiesNode = geoJsonNode.get("properties");
                if (propertiesNode != null) {
                    return new ApiResponse<JsonNode>(true, "Properties retrieved successfully", propertiesNode);
                } else {
                    return new ApiResponse<JsonNode>(false, "No properties found in the GeoJSON data", null);
                }
            } else {
                return new ApiResponse<JsonNode>(false, "No state geolocation found with the given ID.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading properties for state geolocation: ", e);
            return new ApiResponse<JsonNode>(false, "Error reading properties: " + e.getMessage(), null);
        }
    }

    // UPDATE Properties Only
    public ApiResponse<StateGeoLocationDTO> updateProperties(String id, JsonNode newProperties) {
        try {
            String geoJsonData = (String) redisTemplate.opsForHash().get(namespace, id);
            if (geoJsonData == null) {
                return new ApiResponse<StateGeoLocationDTO>(false, "No state geolocation found with the given ID.", null);
            }

            JsonNode geoJsonNode = objectMapper.readTree(geoJsonData);

            if (geoJsonNode instanceof ObjectNode) {
                ((ObjectNode) geoJsonNode).set("properties", newProperties);
                String updatedGeoJsonData = objectMapper.writeValueAsString(geoJsonNode);
                redisTemplate.opsForHash().put(namespace, id, updatedGeoJsonData);

                StateGeoLocationDTO updatedDto = new StateGeoLocationDTO(id, updatedGeoJsonData);
                return new ApiResponse<StateGeoLocationDTO>(true, "State GeoLocation properties updated successfully", updatedDto);
            } else {
                return new ApiResponse<StateGeoLocationDTO>(false, "Invalid GeoJSON format for the state geolocation.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating properties for state geolocation: ", e);
            return new ApiResponse<StateGeoLocationDTO>(false, "Error updating properties: " + e.getMessage(), null);
        }
    }
}
