package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.ContextualizedTermDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.ContextualizedTermDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for ContextualizedTerm operations.
 */
@Service
public class ContextualizedTermService {

    private final ContextualizedTermDAO contextualizedTermDAO;

    @Autowired
    public ContextualizedTermService(ContextualizedTermDAO contextualizedTermDAO) {
        this.contextualizedTermDAO = contextualizedTermDAO;
    }

    public ApiResponse<ContextualizedTermDTO> createContextualizedTerm(ContextualizedTermDTO dto) {
        return contextualizedTermDAO.create(dto);
    }

    public ApiResponse<ContextualizedTermDTO> getContextualizedTermById(String id) {
        return contextualizedTermDAO.read(id);
    }

    public ApiResponse<List<ContextualizedTermDTO>> getAllContextualizedTerms() {
        return contextualizedTermDAO.readAll();
    }

    public ApiResponse<List<ContextualizedTermDTO>> getContextualizedTermsByTermId(String termId) {
        return contextualizedTermDAO.readByTermId(termId);
    }

    public ApiResponse<List<ContextualizedTermDTO>> getContextualizedTermsByContextIds(List<String> contextIds) {
        return contextualizedTermDAO.readByContextIds(contextIds);
    }

    public ApiResponse<ContextualizedTermDTO> updateContextualizedTerm(ContextualizedTermDTO dto) {
        return contextualizedTermDAO.update(dto);
    }

    public ApiResponse<ContextualizedTermDTO> deleteContextualizedTerm(String id) {
        return contextualizedTermDAO.delete(id);
    }
}
