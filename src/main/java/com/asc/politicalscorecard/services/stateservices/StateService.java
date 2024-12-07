package com.asc.politicalscorecard.services.stateservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.statedaos.StateDAO;
import com.asc.politicalscorecard.json.dtos.statedto.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateService {

    private final StateDAO stateDAO;

    @Autowired
    public StateService(StateDAO stateDAO) {
        this.stateDAO = stateDAO;
    }

    // CREATE
    public ApiResponse<StateDTO> createState(StateDTO stateDTO) {
        return stateDAO.create(stateDTO);
    }

    // READ BY ID
    public ApiResponse<StateDTO> getStateById(String id) {
        return stateDAO.read(id);
    }

    // READ ALL
    public ApiResponse<List<StateDTO>> getAllStates() {
        return stateDAO.readAll();
    }

    // UPDATE
    public ApiResponse<StateDTO> updateState(StateDTO stateDTO) {
        System.out.println("In update State");
        return stateDAO.update(stateDTO);
    }

    // DELETE
    public ApiResponse<StateDTO> deleteState(String id) {
        return stateDAO.delete(id);
    }
}
