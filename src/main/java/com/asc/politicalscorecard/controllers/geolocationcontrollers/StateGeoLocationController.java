package com.asc.politicalscorecard.controllers.geolocationcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.statedto.stategeolocationdto.StateGeoLocationDTO;
import com.asc.politicalscorecard.services.stateservices.stategeolocationservices.StateGeoLocationService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stateGeoLocations")
public class StateGeoLocationController 
{
    private final StateGeoLocationService stateGeoLocationService;

    @Autowired
    public StateGeoLocationController(StateGeoLocationService stateGeoLocationService) {
        this.stateGeoLocationService = stateGeoLocationService;
    }

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StateGeoLocationDTO>> createStateGeoLocation(@RequestBody StateGeoLocationDTO dto) {
        ApiResponse<StateGeoLocationDTO> response = stateGeoLocationService.createStateGeoLocation(dto);
        return response.isSuccess() ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // READ by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StateGeoLocationDTO>> getStateGeoLocationById(@PathVariable String id) {
        ApiResponse<StateGeoLocationDTO> response = stateGeoLocationService.getStateGeoLocationById(id);
        return response.isSuccess() ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // READ ALL
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<StateGeoLocationDTO>>> getAllStateGeoLocations() {
        ApiResponse<List<StateGeoLocationDTO>> response = stateGeoLocationService.getAllStateGeoLocations();
        return response.isSuccess() ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StateGeoLocationDTO>> updateStateGeoLocation(
            @PathVariable String id,
            @RequestBody StateGeoLocationDTO dto) {
        dto.setId(id); // Ensure the ID is set correctly
        ApiResponse<StateGeoLocationDTO> response = stateGeoLocationService.updateStateGeoLocation(dto);
        return response.isSuccess() ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<StateGeoLocationDTO>> deleteStateGeoLocation(@PathVariable String id) {
        ApiResponse<StateGeoLocationDTO> response = stateGeoLocationService.deleteStateGeoLocation(id);
        return response.isSuccess() ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // READ Properties Only
    @GetMapping("/{id}/properties")
    public ResponseEntity<ApiResponse<JsonNode>> getPropertiesById(@PathVariable String id) {
        ApiResponse<JsonNode> response = stateGeoLocationService.getPropertiesById(id);
        return response.isSuccess() ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // UPDATE Properties Only
    @PutMapping("/{id}/properties")
    public ResponseEntity<ApiResponse<StateGeoLocationDTO>> updateProperties(
            @PathVariable String id,
            @RequestBody JsonNode newProperties) {
        ApiResponse<StateGeoLocationDTO> response = stateGeoLocationService.updateProperties(id, newProperties);
        return response.isSuccess() ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
