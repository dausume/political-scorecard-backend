package com.asc.politicalscorecard.controllers.locationcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.statedto.StateDTO;
import com.asc.politicalscorecard.services.stateservices.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/states")
public class StateController {

    private final StateService stateService;

    @Autowired
    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    // Baseline Test
    // This is a simple test to ensure that the controller is working correctly
    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("Successfully hit the State Controller.");
    }

    // ------------ Base State Controller ----------
    // CREATE

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StateDTO>> createState(@RequestBody StateDTO stateDTO) {
        ApiResponse<StateDTO> response = stateService.createState(stateDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<StateDTO>>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<ApiResponse<StateDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // READ

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StateDTO>> getStateById(@PathVariable String id) {
        ApiResponse<StateDTO> response = stateService.getStateById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<StateDTO>>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse<StateDTO>>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<StateDTO>>> getAllStates() {
        ApiResponse<List<StateDTO>> response = stateService.getAllStates();
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // UPDATE

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StateDTO>> updateState(@PathVariable String id, @RequestBody StateDTO stateDTO) {
        System.out.println("Inside update State Controller function");
        stateDTO.setId(id); // Ensure the ID is set correctly
        ApiResponse<StateDTO> response = stateService.updateState(stateDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<StateDTO>>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse<StateDTO>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<StateDTO>> deleteState(@PathVariable String id) {
        ApiResponse<StateDTO> response = stateService.deleteState(id);
        if (response.isSuccess()) {
            return new ResponseEntity<ApiResponse<StateDTO>>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse<StateDTO>>(response, HttpStatus.NOT_FOUND);
        }
    }
}