package com.asc.politicalscorecard.controllers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.nationdto.NationDTO;
import com.asc.politicalscorecard.json.dtos.nationdto.NationWithPlanetDTO;
import com.asc.politicalscorecard.services.nationservices.NationService;
import com.asc.politicalscorecard.services.nationservices.NationWithPlanetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<NationDTO>> createNation(@RequestBody NationDTO nationDTO) {
        ApiResponse<NationDTO> response = nationService.createNation(nationDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<NationDTO>>(response, HttpStatus.OK);
        } else {
            // If the nation was not created, return a 500 error
            // In a fully implemented system, all error responses should be handled by exception handling
            // this is for catching any unexpected errors.
            // Exception handling is centralized and generates Failure responses in the GlobalExceptionHandler class.
            return new ResponseEntity<ApiResponse<NationDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // READ

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NationDTO>> getNationById(@PathVariable String id) {
        ApiResponse<NationDTO> response = nationService.getNationById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<NationDTO>>(response, HttpStatus.OK);
        } else {
            // If the nation was not created, return a 500 error
            // In a fully implemented system, all error responses should be handled by exception handling
            // this is for catching any unexpected errors.
            // Exception handling is centralized and generates Failure responses in the GlobalExceptionHandler class.
            return new ResponseEntity<ApiResponse<NationDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("all")
    public ResponseEntity<ApiResponse<List<NationDTO>>> getAllNations() {
        ApiResponse<List<NationDTO>> response = nationService.getAllNations();
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<List<NationDTO>>>(response, HttpStatus.OK);
        } else {
            // If the nation was not created, return a 500 error
            // In a fully implemented system, all error responses should be handled by exception handling
            // this is for catching any unexpected errors.
            // Exception handling is centralized and generates Failure responses in the GlobalExceptionHandler class.
            return new ResponseEntity<ApiResponse<List<NationDTO>>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // UPDATE

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NationDTO>> updateNation(@PathVariable String id, @RequestBody NationDTO nationDTO) {
        System.out.println("Inside update Controller function");
        //nationDTO.setId(id); // Ensure the ID is set correctly
        ApiResponse<NationDTO> response = nationService.updateNation(nationDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<NationDTO>>(response, HttpStatus.OK);
        } 
        else 
        {
            return new ResponseEntity<ApiResponse<NationDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<NationDTO>> deleteNation(@PathVariable String id) {
        ApiResponse<NationDTO> response = nationService.deleteNation(id);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<NationDTO>>(response, HttpStatus.OK);
        } 
        else 
        {
            return new ResponseEntity<ApiResponse<NationDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // ------------ Nation Controller with Related Home Planet ----------

    @GetMapping("/withPlanet/{id}")
    public ResponseEntity<ApiResponse<NationWithPlanetDTO>> getNationWithPlanet(@PathVariable String id) {
        ApiResponse<NationWithPlanetDTO> response = nationWithPlanetService.getNationWithPlanetById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<NationWithPlanetDTO>>(response, HttpStatus.OK);
        } 
        else 
        {
            return new ResponseEntity<ApiResponse<NationWithPlanetDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }
}