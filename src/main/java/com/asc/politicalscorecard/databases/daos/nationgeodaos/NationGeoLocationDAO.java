package com.asc.politicalscorecard.databases.daos.nationgeodaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.nationdto.nationgeolocationdto.NationGeoLocationDTO;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Repository
public class NationGeoLocationDAO {

    private static final Logger logger = Logger.getLogger(NationGeoLocationDAO.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper for JSON parsing
    private final RedisTemplate<String, String> redisTemplate;
    private final String namespace = "geolocation:nations";

    @Autowired
    public NationGeoLocationDAO(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public ApiResponse<NationGeoLocationDTO> create(NationGeoLocationDTO dto) {
        try {
            // Store GeoJSON data as a string in Redis under the given nation key
            String geoJsonData = dto.getGeoJson();
            redisTemplate.opsForHash().put(namespace, dto.getId(), geoJsonData);
            return new ApiResponse<NationGeoLocationDTO>(true, "Nation GeoLocation created successfully", dto);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating nation geolocation: ", e);
            return new ApiResponse<NationGeoLocationDTO>(false, "Error creating nation geolocation: " + e.getMessage(), dto);
        }
    }

    public ApiResponse<NationGeoLocationDTO> read(String id) {
        try {
            // Retrieve GeoJSON data from Redis
            String geoJsonData = (String) redisTemplate.opsForHash().get(namespace, id);
            if (geoJsonData != null) {
                NationGeoLocationDTO dto = new NationGeoLocationDTO(id, geoJsonData);
                return new ApiResponse<NationGeoLocationDTO>(true, "Nation GeoLocation found successfully", dto);
            } else {
                return new ApiResponse<NationGeoLocationDTO>(false, "No nation geolocation found with the given ID.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading nation geolocation: ", e);
            return new ApiResponse<NationGeoLocationDTO>(false, "Error reading nation geolocation: " + e.getMessage(), null);
        }
    }

    // READ Properties Only
    public ApiResponse<JsonNode> readProperties(String id) {
        try {
            // Retrieve GeoJSON data from Redis
            String geoJsonData = (String) redisTemplate.opsForHash().get(namespace, id);
            if (geoJsonData != null) {
                // Parse the GeoJSON data to extract the "properties" field
                JsonNode geoJsonNode = objectMapper.readTree(geoJsonData);
                JsonNode propertiesNode = geoJsonNode.get("properties");

                if (propertiesNode != null) {
                    return new ApiResponse<JsonNode>(true, "Properties retrieved successfully", propertiesNode);
                } else {
                    return new ApiResponse<JsonNode>(false, "No properties found in the GeoJSON data", null);
                }
            } else {
                return new ApiResponse<JsonNode>(false, "No nation geolocation found with the given ID.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading properties for nation geolocation: ", e);
            return new ApiResponse<JsonNode>(false, "Error reading properties: " + e.getMessage(), null);
        }
    }

    public ApiResponse<List<NationGeoLocationDTO>> readAll() {
        try {
            Map<Object, Object> allGeoJsonData = redisTemplate.opsForHash().entries(namespace);
            // Map each entry to NationGeoLocationDTO with both ID and GeoJSON
            List<NationGeoLocationDTO> dtoList = allGeoJsonData.entrySet().stream()
                .map(entry -> new NationGeoLocationDTO(entry.getKey().toString(), entry.getValue().toString()))
                .collect(Collectors.toList());
            return new ApiResponse<List<NationGeoLocationDTO>>(true, "All nations geolocations retrieved successfully", dtoList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading all nation geolocations: ", e);
            return new ApiResponse<List<NationGeoLocationDTO>>(false, "Error reading all nation geolocations: " + e.getMessage(), null);
        }
    }

    public ApiResponse<NationGeoLocationDTO> update(NationGeoLocationDTO dto) 
    {
        try {
            // Update GeoJSON data in Redis
            redisTemplate.opsForHash().put(namespace, dto.getId(), dto.getGeoJson());
            return new ApiResponse<NationGeoLocationDTO>(true, "Nation GeoLocation updated successfully", dto);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating nation geolocation: ", e);
            return new ApiResponse<NationGeoLocationDTO>(false, "Error updating nation geolocation: " + e.getMessage(), dto);
        }
    }

    // UPDATE Properties Only
    public ApiResponse<NationGeoLocationDTO> updateProperties(String id, JsonNode newProperties) {
        try {
            // Retrieve the existing GeoJSON data from Redis
            String geoJsonData = (String) redisTemplate.opsForHash().get(namespace, id);
            if (geoJsonData == null) {
                return new ApiResponse<NationGeoLocationDTO>(false, "No nation geolocation found with the given ID.", null);
            }

            // Parse the GeoJSON data to a JsonNode
            JsonNode geoJsonNode = objectMapper.readTree(geoJsonData);

            // Check if it's an ObjectNode to modify it
            if (geoJsonNode instanceof ObjectNode) {
                // Replace the "properties" section with the new properties
                ((ObjectNode) geoJsonNode).set("properties", newProperties);

                // Convert the updated JsonNode back to a JSON string
                String updatedGeoJsonData = objectMapper.writeValueAsString(geoJsonNode);

                // Update the data in Redis
                redisTemplate.opsForHash().put(namespace, id, updatedGeoJsonData);

                // Return the updated GeoJSON in a DTO
                NationGeoLocationDTO updatedDto = new NationGeoLocationDTO(id, updatedGeoJsonData);
                return new ApiResponse<NationGeoLocationDTO>(true, "Nation GeoLocation properties updated successfully", updatedDto);
            } else {
                return new ApiResponse<NationGeoLocationDTO>(false, "Invalid GeoJSON format for the nation geolocation.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating properties for nation geolocation: ", e);
            return new ApiResponse<>(false, "Error updating properties: " + e.getMessage(), null);
        }
    }

    public ApiResponse<NationGeoLocationDTO> delete(String id) {
        try {
            Long rowsDeleted = redisTemplate.opsForHash().delete(namespace, id);
            if (rowsDeleted > 0) {
                return new ApiResponse<NationGeoLocationDTO>(true, "Nation GeoLocation deleted successfully", null);
            } else {
                return new ApiResponse<NationGeoLocationDTO>(false, "Failed to delete nation geolocation.", null);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting nation geolocation: ", e);
            return new ApiResponse<NationGeoLocationDTO>(false, "Error deleting nation geolocation: " + e.getMessage(), null);
        }
    }
}