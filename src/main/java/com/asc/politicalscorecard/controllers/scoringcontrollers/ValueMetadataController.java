package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.ValueMetadataDTO;
import com.asc.politicalscorecard.services.scoringservices.ValueMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for ValueMetadata operations.
 * Base path: /api/value-metadata
 */
@RestController
@RequestMapping("/api/value-metadata")
public class ValueMetadataController {

    private final ValueMetadataService valueMetadataService;

    @Autowired
    public ValueMetadataController(ValueMetadataService valueMetadataService) {
        this.valueMetadataService = valueMetadataService;
    }

    /**
     * Baseline test endpoint
     * GET /api/value-metadata
     */
    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("Successfully hit the ValueMetadata Controller.");
    }

    /**
     * Create a new value metadata
     * POST /api/value-metadata/create
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ValueMetadataDTO>> createValueMetadata(@RequestBody ValueMetadataDTO dto) {
        ApiResponse<ValueMetadataDTO> response = valueMetadataService.createValueMetadata(dto);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get value metadata by ID
     * GET /api/value-metadata/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ValueMetadataDTO>> getValueMetadataById(@PathVariable String id) {
        ApiResponse<ValueMetadataDTO> response = valueMetadataService.getValueMetadataById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all value metadata
     * GET /api/value-metadata/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ValueMetadataDTO>>> getAllValueMetadata() {
        ApiResponse<List<ValueMetadataDTO>> response = valueMetadataService.getAllValueMetadata();
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing value metadata
     * PUT /api/value-metadata/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ValueMetadataDTO>> updateValueMetadata(
            @PathVariable String id,
            @RequestBody ValueMetadataDTO dto) {
        dto.setId(id);
        ApiResponse<ValueMetadataDTO> response = valueMetadataService.updateValueMetadata(dto);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a value metadata by ID
     * DELETE /api/value-metadata/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ValueMetadataDTO>> deleteValueMetadata(@PathVariable String id) {
        ApiResponse<ValueMetadataDTO> response = valueMetadataService.deleteValueMetadata(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
