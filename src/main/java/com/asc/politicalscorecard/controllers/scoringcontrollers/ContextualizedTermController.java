package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.ContextualizedTermDTO;
import com.asc.politicalscorecard.services.scoringservices.ContextualizedTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for ContextualizedTerm operations.
 * Base path: /api/contextualized-terms
 */
@RestController
@RequestMapping("/api/contextualized-terms")
public class ContextualizedTermController {

    private final ContextualizedTermService contextualizedTermService;

    @Autowired
    public ContextualizedTermController(ContextualizedTermService contextualizedTermService) {
        this.contextualizedTermService = contextualizedTermService;
    }

    /**
     * Baseline test endpoint
     * GET /api/contextualized-terms
     */
    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("Successfully hit the ContextualizedTerm Controller.");
    }

    /**
     * Create a new contextualized term
     * POST /api/contextualized-terms/create
     * Body: ContextualizedTermDTO with termId, contextIds, valueMetadataId, preNormalizedValue, postNormalizedValue
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ContextualizedTermDTO>> createContextualizedTerm(@RequestBody ContextualizedTermDTO dto) {
        ApiResponse<ContextualizedTermDTO> response = contextualizedTermService.createContextualizedTerm(dto);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get contextualized term by ID
     * GET /api/contextualized-terms/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContextualizedTermDTO>> getContextualizedTermById(@PathVariable String id) {
        ApiResponse<ContextualizedTermDTO> response = contextualizedTermService.getContextualizedTermById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all contextualized terms
     * GET /api/contextualized-terms/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ContextualizedTermDTO>>> getAllContextualizedTerms() {
        ApiResponse<List<ContextualizedTermDTO>> response = contextualizedTermService.getAllContextualizedTerms();
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get contextualized terms by base term ID
     * GET /api/contextualized-terms/by-term/{termId}
     */
    @GetMapping("/by-term/{termId}")
    public ResponseEntity<ApiResponse<List<ContextualizedTermDTO>>> getContextualizedTermsByTermId(@PathVariable String termId) {
        ApiResponse<List<ContextualizedTermDTO>> response = contextualizedTermService.getContextualizedTermsByTermId(termId);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Update an existing contextualized term
     * PUT /api/contextualized-terms/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContextualizedTermDTO>> updateContextualizedTerm(
            @PathVariable String id,
            @RequestBody ContextualizedTermDTO dto) {
        dto.setId(id);
        ApiResponse<ContextualizedTermDTO> response = contextualizedTermService.updateContextualizedTerm(dto);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a contextualized term by ID
     * DELETE /api/contextualized-terms/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ContextualizedTermDTO>> deleteContextualizedTerm(@PathVariable String id) {
        ApiResponse<ContextualizedTermDTO> response = contextualizedTermService.deleteContextualizedTerm(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
