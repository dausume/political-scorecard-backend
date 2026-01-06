package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.WorldviewElectionDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.WorldviewElectionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for WorldviewElection operations.
 * Provides business logic for worldview election management.
 */
@Service
public class WorldviewElectionService {

    private final WorldviewElectionDAO worldviewElectionDAO;

    @Autowired
    public WorldviewElectionService(WorldviewElectionDAO worldviewElectionDAO) {
        this.worldviewElectionDAO = worldviewElectionDAO;
    }

    // CREATE
    public ApiResponse<WorldviewElectionDTO> createElection(WorldviewElectionDTO electionDTO) {
        return worldviewElectionDAO.create(electionDTO);
    }

    // READ BY ID
    public ApiResponse<WorldviewElectionDTO> getElectionById(String id) {
        return worldviewElectionDAO.read(id);
    }

    // READ ALL
    public ApiResponse<List<WorldviewElectionDTO>> getAllElections() {
        return worldviewElectionDAO.readAll();
    }

    // READ BY STATUS
    public ApiResponse<List<WorldviewElectionDTO>> getElectionsByStatus(String status) {
        return worldviewElectionDAO.readByStatus(status);
    }

    // UPDATE
    public ApiResponse<WorldviewElectionDTO> updateElection(WorldviewElectionDTO electionDTO) {
        return worldviewElectionDAO.update(electionDTO);
    }

    // DELETE
    public ApiResponse<WorldviewElectionDTO> deleteElection(String id) {
        return worldviewElectionDAO.delete(id);
    }
}
