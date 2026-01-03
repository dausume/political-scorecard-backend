package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.*;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;
import com.asc.politicalscorecard.services.scoringservices.ContextHierarchyService;
import com.asc.politicalscorecard.services.scoringservices.TermContextService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for TermContext operations.
 * Provides endpoints for creating and reading different types of contexts.
 * Base path: /api/contexts
 */
@RestController
@RequestMapping("/api/contexts")
public class TermContextController {

    private final TermContextService termContextService;
    private final ContextHierarchyService hierarchyService;

    @Autowired
    public TermContextController(TermContextService termContextService, ContextHierarchyService hierarchyService) {
        this.termContextService = termContextService;
        this.hierarchyService = hierarchyService;
    }

    /**
     * Baseline test endpoint
     * GET /api/contexts
     */
    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("Successfully hit the TermContext Controller.");
    }

    /**
     * Create a new context (polymorphic - supports all context types)
     * POST /api/contexts/create
     * Body: Any TermContextDTO subclass JSON (TimeframeContextDTO, LocationContextDTO, etc.)
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TermContextDTO>> createContext(@RequestBody TermContextDTO contextDTO) {
        ApiResponse<TermContextDTO> response = termContextService.createContext(contextDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get context by ID
     * GET /api/contexts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TermContextDTO>> getContextById(@PathVariable String id) {
        ApiResponse<TermContextDTO> response = termContextService.getContextById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all contexts
     * GET /api/contexts/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TermContextDTO>>> getAllContexts() {
        ApiResponse<List<TermContextDTO>> response = termContextService.getAllContexts();
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get contexts by type
     * GET /api/contexts/type/{type}
     * Types: TIMEFRAME, LOCATION, DEMOGRAPHIC, ECONOMIC, CUSTOM
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<TermContextDTO>>> getContextsByType(@PathVariable String type) {
        try {
            TermContextType contextType = TermContextType.valueOf(type.toUpperCase());
            ApiResponse<List<TermContextDTO>> response = termContextService.getContextsByType(contextType);
            if (response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Invalid context type: " + type, null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Update an existing context
     * PUT /api/contexts/{id}
     * Body: TermContextDTO JSON
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TermContextDTO>> updateContext(
            @PathVariable String id,
            @RequestBody TermContextDTO contextDTO) {
        contextDTO.setId(id);
        ApiResponse<TermContextDTO> response = termContextService.updateContext(contextDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a context by ID
     * DELETE /api/contexts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<TermContextDTO>> deleteContext(@PathVariable String id) {
        ApiResponse<TermContextDTO> response = termContextService.deleteContext(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // ==================== Hierarchy Endpoints ====================

    /**
     * Get complete hierarchy chain for a context
     * GET /api/contexts/{id}/hierarchy
     * Returns ancestors (less specific) and descendants (more specific)
     * Example: For "California" -> [USA (parent), California (self), San Francisco (child)]
     */
    @GetMapping("/{id}/hierarchy")
    public ResponseEntity<ApiResponse<ContextHierarchyChainDTO>> getHierarchyChain(@PathVariable String id) {
        ApiResponse<ContextHierarchyChainDTO> response = hierarchyService.getHierarchyChain(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get parent contexts for a given context (less specific)
     * GET /api/contexts/{id}/parents
     */
    @GetMapping("/{id}/parents")
    public ResponseEntity<ApiResponse<List<ContextHierarchyDTO>>> getParents(@PathVariable String id) {
        ApiResponse<List<ContextHierarchyDTO>> response = hierarchyService.getParents(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get child contexts for a given context (more specific)
     * GET /api/contexts/{id}/children
     */
    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse<List<ContextHierarchyDTO>>> getChildren(@PathVariable String id) {
        ApiResponse<List<ContextHierarchyDTO>> response = hierarchyService.getChildren(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Create a hierarchy relationship between two contexts
     * POST /api/contexts/hierarchy/create
     * Body: ContextHierarchyDTO JSON with parentContextId and childContextId
     */
    @PostMapping("/hierarchy/create")
    public ResponseEntity<ApiResponse<ContextHierarchyDTO>> createHierarchy(@RequestBody ContextHierarchyDTO hierarchyDTO) {
        ApiResponse<ContextHierarchyDTO> response = hierarchyService.createHierarchy(hierarchyDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all hierarchy relationships
     * GET /api/contexts/hierarchy/all
     */
    @GetMapping("/hierarchy/all")
    public ResponseEntity<ApiResponse<List<ContextHierarchyDTO>>> getAllHierarchies() {
        ApiResponse<List<ContextHierarchyDTO>> response = hierarchyService.getAllHierarchies();
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a hierarchy relationship
     * DELETE /api/contexts/hierarchy/{id}
     */
    @DeleteMapping("/hierarchy/{id}")
    public ResponseEntity<ApiResponse<ContextHierarchyDTO>> deleteHierarchy(@PathVariable String id) {
        ApiResponse<ContextHierarchyDTO> response = hierarchyService.deleteHierarchy(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
