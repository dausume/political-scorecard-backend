package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.TermDTO;
import com.asc.politicalscorecard.services.scoringservices.TermService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Term operations.
 * Provides endpoints for CRUD operations on terms.
 * Base path: /api/terms
 */
@RestController
@RequestMapping("/api/terms")
public class TermController {

    private final TermService termService;

    @Autowired
    public TermController(TermService termService) {
        this.termService = termService;
    }

    /**
     * Baseline test endpoint
     * GET /api/terms
     */
    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("Successfully hit the Term Controller.");
    }

    /**
     * Create a new term
     * POST /api/terms/create
     * Body: TermDTO JSON
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TermDTO>> createTerm(@RequestBody TermDTO termDTO) {
        ApiResponse<TermDTO> response = termService.createTerm(termDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get term by ID
     * GET /api/terms/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TermDTO>> getTermById(@PathVariable String id) {
        ApiResponse<TermDTO> response = termService.getTermById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all terms
     * GET /api/terms/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TermDTO>>> getAllTerms() {
        ApiResponse<List<TermDTO>> response = termService.getAllTerms();
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get terms by category
     * GET /api/terms/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<TermDTO>>> getTermsByCategory(@PathVariable String category) {
        ApiResponse<List<TermDTO>> response = termService.getTermsByCategory(category);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing term
     * PUT /api/terms/{id}
     * Body: TermDTO JSON
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TermDTO>> updateTerm(@PathVariable String id, @RequestBody TermDTO termDTO) {
        termDTO.setId(id); // Ensure the ID from path is used
        ApiResponse<TermDTO> response = termService.updateTerm(termDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a term by ID
     * DELETE /api/terms/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<TermDTO>> deleteTerm(@PathVariable String id) {
        ApiResponse<TermDTO> response = termService.deleteTerm(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
