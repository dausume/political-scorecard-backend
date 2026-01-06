package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.WorldviewElectionDTO;
import com.asc.politicalscorecard.services.scoringservices.WorldviewElectionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for WorldviewElection operations.
 * Provides endpoints for CRUD operations on worldview elections.
 * Base path: /api/worldview-elections
 */
@RestController
@RequestMapping("/api/worldview-elections")
public class WorldviewElectionController {

    private final WorldviewElectionService worldviewElectionService;

    @Autowired
    public WorldviewElectionController(WorldviewElectionService worldviewElectionService) {
        this.worldviewElectionService = worldviewElectionService;
    }

    /**
     * Baseline test endpoint
     * GET /api/worldview-elections
     */
    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("Successfully hit the WorldviewElection Controller.");
    }

    /**
     * Create a new worldview election
     * POST /api/worldview-elections/create
     * Body: WorldviewElectionDTO JSON
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<WorldviewElectionDTO>> createElection(@RequestBody WorldviewElectionDTO electionDTO) {
        ApiResponse<WorldviewElectionDTO> response = worldviewElectionService.createElection(electionDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get election by ID
     * GET /api/worldview-elections/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorldviewElectionDTO>> getElectionById(@PathVariable String id) {
        ApiResponse<WorldviewElectionDTO> response = worldviewElectionService.getElectionById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all elections
     * GET /api/worldview-elections/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<WorldviewElectionDTO>>> getAllElections() {
        ApiResponse<List<WorldviewElectionDTO>> response = worldviewElectionService.getAllElections();
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get elections by status
     * GET /api/worldview-elections/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<WorldviewElectionDTO>>> getElectionsByStatus(@PathVariable String status) {
        ApiResponse<List<WorldviewElectionDTO>> response = worldviewElectionService.getElectionsByStatus(status);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing election
     * PUT /api/worldview-elections/{id}
     * Body: WorldviewElectionDTO JSON
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorldviewElectionDTO>> updateElection(@PathVariable String id, @RequestBody WorldviewElectionDTO electionDTO) {
        electionDTO.setId(id); // Ensure the ID from path is used
        ApiResponse<WorldviewElectionDTO> response = worldviewElectionService.updateElection(electionDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete an election by ID
     * DELETE /api/worldview-elections/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<WorldviewElectionDTO>> deleteElection(@PathVariable String id) {
        ApiResponse<WorldviewElectionDTO> response = worldviewElectionService.deleteElection(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
