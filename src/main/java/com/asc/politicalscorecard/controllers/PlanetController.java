package com.asc.politicalscorecard.controllers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.nationdto.NationDTO;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;
import com.asc.politicalscorecard.services.PlanetService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@RestController
@RequestMapping("planet")
public class PlanetController 
{

    @Autowired
    private PlanetService planetService;

    // Used to test if this controller itself is accessible
    @GetMapping("")
    public String pingPlanetController() {
        return "Successfully pinged the Planet Controller, thou now control the forces of nature!! (or at least planetary metadata)";
    }

    // CREATE

    // Used to create a Single Planet Object.
    @PostMapping("create")
    public ResponseEntity<ApiResponse<PlanetDTO>> createPlanet(@RequestBody PlanetDTO planetDTO) {
        System.out.println("In create planet controller.");
        ApiResponse<PlanetDTO> response = planetService.createPlanet(planetDTO);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else { // Catches any unexpected errors.
            return ResponseEntity.status(500).body(response);
        }
    }

    // READ
    // Used to retrieve all Planet Objects.
    @GetMapping("all")
    public List<PlanetDTO> getAllPlanets() {
        return planetService.getAllPlanets();
    }

    // Used to retrieve a single Planet Object, if the id sent has a corresponding planet object.
    @GetMapping("{id}")
    public PlanetDTO getPlanet(@PathVariable String id) {
        return planetService.getPlanet(id);
    }

    // UPDATE
    // Used to update a Single Planet Object.
    @PostMapping("update")
    public String updatePlanet(@RequestBody PlanetDTO planetDTO) {
        if (planetService.updatePlanet(planetDTO)) {
            return "Planet updated successfully!";
        } else {
            return "Failed to update planet.";
        }
    }

    // DELETE
    // Used to delete a Single Planet Object.
    @DeleteMapping("delete/{id}")
    public String deletePlanet(@PathVariable String id) {
        if (planetService.deletePlanet(id)) {
            return "Planet deleted successfully!";
        } else {
            return "Failed to delete planet.";
        }
    }
    
}