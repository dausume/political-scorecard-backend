package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.DebateMessageDTO;
import com.asc.politicalscorecard.services.scoringservices.DebateMessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket Controller for real-time debate messaging
 * Handles STOMP messages for debate functionality
 *
 * Message flow:
 * 1. Client sends message to /app/debate/{electionId}/send
 * 2. Server processes and saves to database
 * 3. Server broadcasts to /topic/debate/{electionId}
 * 4. All subscribed clients receive the message
 */
@Controller
public class DebateWebSocketController {

    private final DebateMessageService debateMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public DebateWebSocketController(
            DebateMessageService debateMessageService,
            SimpMessagingTemplate messagingTemplate) {
        this.debateMessageService = debateMessageService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handle new message sent from client
     * Endpoint: /app/debate/{electionId}/send
     * Broadcasts to: /topic/debate/{electionId}
     */
    @MessageMapping("/debate/{electionId}/send")
    @SendTo("/topic/debate/{electionId}")
    public DebateMessageDTO sendMessage(
            @DestinationVariable String electionId,
            DebateMessageDTO message) {

        System.out.println("WebSocket: Received message for election " + electionId);

        // Save message to database via service
        ApiResponse<DebateMessageDTO> response = debateMessageService.createMessage(message);

        if (response.isSuccess() && response.getData() != null) {
            System.out.println("WebSocket: Message saved, broadcasting to subscribers");
            return response.getData();
        } else {
            System.err.println("WebSocket: Failed to save message - " + response.getMessage());
            throw new RuntimeException("Failed to save message: " + response.getMessage());
        }
    }

    /**
     * Handle message update from client
     * Endpoint: /app/debate/{electionId}/update
     * Broadcasts to: /topic/debate/{electionId}/updated
     */
    @MessageMapping("/debate/{electionId}/update")
    public void updateMessage(
            @DestinationVariable String electionId,
            DebateMessageDTO message) {

        System.out.println("WebSocket: Updating message " + message.getId() + " for election " + electionId);

        // Update message in database
        ApiResponse<DebateMessageDTO> response = debateMessageService.updateMessage(message);

        if (response.isSuccess() && response.getData() != null) {
            System.out.println("WebSocket: Message updated, broadcasting to subscribers");
            // Broadcast the updated message
            messagingTemplate.convertAndSend(
                "/topic/debate/" + electionId + "/updated",
                response.getData()
            );
        } else {
            System.err.println("WebSocket: Failed to update message - " + response.getMessage());
        }
    }

    /**
     * Handle message deletion from client
     * Endpoint: /app/debate/{electionId}/delete
     * Broadcasts to: /topic/debate/{electionId}/deleted
     */
    @MessageMapping("/debate/{electionId}/delete")
    public void deleteMessage(
            @DestinationVariable String electionId,
            String messageId) {

        System.out.println("WebSocket: Deleting message " + messageId + " for election " + electionId);

        // Delete (soft delete) message in database
        ApiResponse<DebateMessageDTO> response = debateMessageService.deleteMessage(messageId);

        if (response.isSuccess()) {
            System.out.println("WebSocket: Message deleted, broadcasting to subscribers");
            // Broadcast the deletion event with just the messageId
            messagingTemplate.convertAndSend(
                "/topic/debate/" + electionId + "/deleted",
                messageId
            );
        } else {
            System.err.println("WebSocket: Failed to delete message - " + response.getMessage());
        }
    }
}
