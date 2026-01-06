package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.DebateMessageDTO;
import com.asc.politicalscorecard.services.scoringservices.DebateMessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for DebateMessage operations.
 * Provides endpoints for CRUD operations on election debate messages.
 * Base path: /api/debate-messages
 */
@RestController
@RequestMapping("/api/debate-messages")
public class DebateMessageController {

    private final DebateMessageService debateMessageService;

    @Autowired
    public DebateMessageController(DebateMessageService debateMessageService) {
        this.debateMessageService = debateMessageService;
    }

    /**
     * Baseline test endpoint
     * GET /api/debate-messages
     */
    @GetMapping("")
    public ResponseEntity<String> baseline() {
        return ResponseEntity.ok("Successfully hit the DebateMessage Controller.");
    }

    /**
     * Create a new debate message
     * POST /api/debate-messages/create
     * Body: DebateMessageDTO JSON
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<DebateMessageDTO>> createMessage(@RequestBody DebateMessageDTO messageDTO) {
        ApiResponse<DebateMessageDTO> response = debateMessageService.createMessage(messageDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get message by ID
     * GET /api/debate-messages/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DebateMessageDTO>> getMessageById(@PathVariable String id) {
        ApiResponse<DebateMessageDTO> response = debateMessageService.getMessageById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get all messages
     * GET /api/debate-messages/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DebateMessageDTO>>> getAllMessages() {
        ApiResponse<List<DebateMessageDTO>> response = debateMessageService.getAllMessages();
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get messages by election ID
     * GET /api/debate-messages/election/{electionId}
     */
    @GetMapping("/election/{electionId}")
    public ResponseEntity<ApiResponse<List<DebateMessageDTO>>> getMessagesByElectionId(@PathVariable String electionId) {
        ApiResponse<List<DebateMessageDTO>> response = debateMessageService.getMessagesByElectionId(electionId);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing message
     * PUT /api/debate-messages/{id}
     * Body: DebateMessageDTO JSON
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DebateMessageDTO>> updateMessage(@PathVariable String id, @RequestBody DebateMessageDTO messageDTO) {
        messageDTO.setId(id); // Ensure the ID from path is used
        ApiResponse<DebateMessageDTO> response = debateMessageService.updateMessage(messageDTO);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a message by ID (soft delete)
     * DELETE /api/debate-messages/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DebateMessageDTO>> deleteMessage(@PathVariable String id) {
        ApiResponse<DebateMessageDTO> response = debateMessageService.deleteMessage(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
