package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.DebateMessageDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.DebateMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for DebateMessage operations.
 * Provides business logic for debate message management.
 */
@Service
public class DebateMessageService {

    private final DebateMessageDAO debateMessageDAO;

    @Autowired
    public DebateMessageService(DebateMessageDAO debateMessageDAO) {
        this.debateMessageDAO = debateMessageDAO;
    }

    // CREATE
    public ApiResponse<DebateMessageDTO> createMessage(DebateMessageDTO messageDTO) {
        return debateMessageDAO.create(messageDTO);
    }

    // READ BY ID
    public ApiResponse<DebateMessageDTO> getMessageById(String id) {
        return debateMessageDAO.read(id);
    }

    // READ ALL
    public ApiResponse<List<DebateMessageDTO>> getAllMessages() {
        return debateMessageDAO.readAll();
    }

    // READ BY ELECTION ID
    public ApiResponse<List<DebateMessageDTO>> getMessagesByElectionId(String electionId) {
        return debateMessageDAO.readByElectionId(electionId);
    }

    // UPDATE
    public ApiResponse<DebateMessageDTO> updateMessage(DebateMessageDTO messageDTO) {
        return debateMessageDAO.update(messageDTO);
    }

    // DELETE (soft delete)
    public ApiResponse<DebateMessageDTO> deleteMessage(String id) {
        return debateMessageDAO.delete(id);
    }
}
