package com.asc.politicalscorecard.services.stateservices.stategeolocationservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.stategeodaos.StateGeoLocationDAO;
import com.asc.politicalscorecard.json.dtos.statedto.stategeolocationdto.StateGeoLocationDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateGeoLocationService {

    private final StateGeoLocationDAO stateGeoLocationDAO;

    @Autowired
    public StateGeoLocationService(StateGeoLocationDAO stateGeoLocationDAO) {
        this.stateGeoLocationDAO = stateGeoLocationDAO;
    }

    // CREATE
    public ApiResponse<StateGeoLocationDTO> createStateGeoLocation(StateGeoLocationDTO dto) {
        return stateGeoLocationDAO.create(dto);
    }

    // READ by ID
    public ApiResponse<StateGeoLocationDTO> getStateGeoLocationById(String id) {
        return stateGeoLocationDAO.read(id);
    }

    // READ ALL
    public ApiResponse<List<StateGeoLocationDTO>> getAllStateGeoLocations() {
        return stateGeoLocationDAO.readAll();
    }

    // UPDATE
    public ApiResponse<StateGeoLocationDTO> updateStateGeoLocation(StateGeoLocationDTO dto) {
        return stateGeoLocationDAO.update(dto);
    }

    // DELETE
    public ApiResponse<StateGeoLocationDTO> deleteStateGeoLocation(String id) {
        return stateGeoLocationDAO.delete(id);
    }

    // READ Properties Only
    public ApiResponse<JsonNode> getPropertiesById(String id) {
        return stateGeoLocationDAO.readProperties(id);
    }

    // UPDATE Properties Only
    public ApiResponse<StateGeoLocationDTO> updateProperties(String id, JsonNode newProperties) {
        return stateGeoLocationDAO.updateProperties(id, newProperties);
    }
}
