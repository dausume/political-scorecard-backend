package com.asc.politicalscorecard.databases.daos.nationgeodaos;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.exceptions.apiuserexceptions.InvalidInputException;
import com.asc.politicalscorecard.json.dtos.nationgeolocationdto.NationGeoLocationDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class NationGeoLocationDAO {

    private static final Logger logger = Logger.getLogger(NationGeoLocationDAO.class.getName());
    private final RedisTemplate<String, String> redisTemplate;
    private final String namespace = "geoLocations:nations";

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

    public ApiResponse<List<NationGeoLocationDTO>> readAll() {
        try {
            List<Object> allGeoJsonData = redisTemplate.opsForHash().values(namespace);
            List<NationGeoLocationDTO> dtoList = allGeoJsonData.stream()
                .map(data -> new NationGeoLocationDTO(data.toString()))
                .toList();
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
