package com.asc.politicalscorecard.controllers.geolocationcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.nationdto.nationgeolocationdto.NationGeoLocationDTO;
import com.asc.politicalscorecard.services.nationservices.nationgeolocationservices.NationGeoLocationService;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nationGeoLocations")
public class NationGeoLocationController {

    private final NationGeoLocationService nationGeoLocationService;

    @Autowired
    public NationGeoLocationController(NationGeoLocationService nationGeoLocationService) {
        this.nationGeoLocationService = nationGeoLocationService;
    }

    // Baseline Test
    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("Successfully hit the Nation GeoLocation Controller.");
    }

    // ------------ Nation GeoLocation CRUD Operations ------------

    // CREATE

    @PostMapping("create")
    public ResponseEntity<ApiResponse<NationGeoLocationDTO>> createNationGeoLocation(@RequestBody NationGeoLocationDTO nationGeoLocationDTO) {
        ApiResponse<NationGeoLocationDTO> response = nationGeoLocationService.createNationGeoLocation(nationGeoLocationDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<NationGeoLocationDTO>>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse<NationGeoLocationDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // READ (Retrieve a single Nation GeoLocation by ID)

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NationGeoLocationDTO>> getNationGeoLocationById(@PathVariable String id) {
        ApiResponse<NationGeoLocationDTO> response = nationGeoLocationService.getNationGeoLocationById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<NationGeoLocationDTO>>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse<NationGeoLocationDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // READ ALL (Retrieve all Nation GeoLocations)

    @GetMapping("all")
    public ResponseEntity<ApiResponse<List<NationGeoLocationDTO>>> getAllNationGeoLocations() {
        ApiResponse<List<NationGeoLocationDTO>> response = nationGeoLocationService.getAllNationGeoLocations();
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<List<NationGeoLocationDTO>>>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse<List<NationGeoLocationDTO>>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint to retrieve only the properties of a nation by ID
    @GetMapping("/{id}/properties")
    public ResponseEntity<ApiResponse<JsonNode>> getPropertiesById(@PathVariable String id) {
        ApiResponse<JsonNode> response = nationGeoLocationService.getPropertiesById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // UPDATE

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NationGeoLocationDTO>> updateNationGeoLocation(@PathVariable String id, @RequestBody NationGeoLocationDTO nationGeoLocationDTO) {
        nationGeoLocationDTO.setId(id); // Ensure the ID is set correctly
        ApiResponse<NationGeoLocationDTO> response = nationGeoLocationService.updateNationGeoLocation(nationGeoLocationDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<NationGeoLocationDTO>>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse<NationGeoLocationDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint to update only the properties of a nation by ID
    @PutMapping("/{id}/properties")
    public ResponseEntity<ApiResponse<NationGeoLocationDTO>> updateProperties(
            @PathVariable String id,
            @RequestBody JsonNode newProperties) {
        ApiResponse<NationGeoLocationDTO> response = nationGeoLocationService.updateProperties(id, newProperties);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<NationGeoLocationDTO>>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse<NationGeoLocationDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<NationGeoLocationDTO>> deleteNationGeoLocation(@PathVariable String id) {
        ApiResponse<NationGeoLocationDTO> response = nationGeoLocationService.deleteNationGeoLocation(id);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<NationGeoLocationDTO>>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse<NationGeoLocationDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
