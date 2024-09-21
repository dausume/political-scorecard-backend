package com.asc.politicalscorecard.controllers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.nationdto.NationDTO;
import com.asc.politicalscorecard.json.dtos.nationdto.NationWithPlanetDTO;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.asc.politicalscorecard.services.nationservices.NationService;
import com.asc.politicalscorecard.services.nationservices.NationWithPlanetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nations")
public class NationController {

    private final NationService nationService;
    private final NationWithPlanetService nationWithPlanetService;

    @Autowired
    public NationController(NationService nationService, NationWithPlanetService nationWithPlanetService) {
        this.nationService = nationService;
        this.nationWithPlanetService = nationWithPlanetService;
    }

    // Baseline Test
    // This is a simple test to ensure that the controller is working correctly
    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("Successfully hit the Nation Controller.");
    }

    // ------------ Base Nation Controller with No Related Objects ----------
    // CREATE

    @PostMapping("create")
    public ResponseEntity<NationDTO> createNation(@RequestBody NationDTO nationDTO) {
        ApiResponse<NationDTO> response = nationService.createNation(nationDTO);
        if (response.isSuccess()) {
            return ResponseEntity.ok(nationDTO);
        } else {
            // If the nation was not created, return a 500 error
            // In a fully implemented system, all error responses should be handled by exception handling
            // this is for catching any unexpected errors.
            // Exception handling is centralized and generates Failure responses in the GlobalExceptionHandler class.
            return ResponseEntity.status(500).build();
        }
    }

    // READ

    @GetMapping("/{id}")
    public ResponseEntity<NationDTO> getNationById(@PathVariable String id) {
        NationDTO nationDTO = nationService.getNationById(id);
        if (nationDTO != null) {
            return ResponseEntity.ok(nationDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("all")
    public ResponseEntity<List<NationDTO>> getAllNations() {
        List<NationDTO> nations = nationService.getAllNations();
        return ResponseEntity.ok(nations);
    }

    // UPDATE

    @PutMapping("/{id}")
    public ResponseEntity<NationDTO> updateNation(@PathVariable String id, @RequestBody NationDTO nationDTO) {
        System.out.println("Inside update Controller function");
        nationDTO.setId(id); // Ensure the ID is set correctly
        if (nationService.updateNation(nationDTO)) {
            return ResponseEntity.ok(nationDTO);
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    // DELETE

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNation(@PathVariable String id) {
        if (nationService.deleteNation(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    // ------------ Nation Controller with Related Home Planet ----------

    @GetMapping("/withPlanet/{id}")
    public NationWithPlanetDTO getNationWithPlanet(@PathVariable String id) {
        return nationWithPlanetService.getNationWithPlanetById(id);
    }
}