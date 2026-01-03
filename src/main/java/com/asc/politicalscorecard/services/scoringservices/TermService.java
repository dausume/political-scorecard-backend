package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.TermDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.TermDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for Term operations.
 * Provides business logic for term management.
 */
@Service
public class TermService {

    private final TermDAO termDAO;

    @Autowired
    public TermService(TermDAO termDAO) {
        this.termDAO = termDAO;
    }

    // CREATE
    public ApiResponse<TermDTO> createTerm(TermDTO termDTO) {
        return termDAO.create(termDTO);
    }

    // READ BY ID
    public ApiResponse<TermDTO> getTermById(String id) {
        return termDAO.read(id);
    }

    // READ ALL
    public ApiResponse<List<TermDTO>> getAllTerms() {
        return termDAO.readAll();
    }

    // READ BY CATEGORY
    public ApiResponse<List<TermDTO>> getTermsByCategory(String category) {
        return termDAO.readByCategory(category);
    }

    // UPDATE
    public ApiResponse<TermDTO> updateTerm(TermDTO termDTO) {
        return termDAO.update(termDTO);
    }

    // DELETE
    public ApiResponse<TermDTO> deleteTerm(String id) {
        return termDAO.delete(id);
    }
}
