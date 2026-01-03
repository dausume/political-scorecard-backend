package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.TermContextDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.TermContextDTO;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for TermContext operations.
 */
@Service
public class TermContextService {

    private final TermContextDAO termContextDAO;

    @Autowired
    public TermContextService(TermContextDAO termContextDAO) {
        this.termContextDAO = termContextDAO;
    }

    public ApiResponse<TermContextDTO> createContext(TermContextDTO contextDTO) {
        return termContextDAO.create(contextDTO);
    }

    public ApiResponse<TermContextDTO> getContextById(String id) {
        return termContextDAO.read(id);
    }

    public ApiResponse<List<TermContextDTO>> getAllContexts() {
        return termContextDAO.readAll();
    }

    public ApiResponse<List<TermContextDTO>> getContextsByType(TermContextType type) {
        return termContextDAO.readByType(type);
    }

    public ApiResponse<TermContextDTO> updateContext(TermContextDTO contextDTO) {
        return termContextDAO.update(contextDTO);
    }

    public ApiResponse<TermContextDTO> deleteContext(String id) {
        return termContextDAO.delete(id);
    }
}
