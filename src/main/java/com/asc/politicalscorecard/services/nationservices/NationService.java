package com.asc.politicalscorecard.services.nationservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.nationdaos.NationDAO;
import com.asc.politicalscorecard.json.dtos.nationdto.NationDTO;
import com.asc.politicalscorecard.objects.location.Nation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NationService {

    private final NationDAO nationDAO;

    @Autowired
    public NationService(NationDAO nationDAO) {
        this.nationDAO = nationDAO;
    }

    public ApiResponse<NationDTO> createNation(NationDTO nationDTO) {
        return nationDAO.create(nationDTO);
    }

    public ApiResponse<NationDTO> getNationById(String id) {
        return nationDAO.read(id);
    }

    public ApiResponse<List<NationDTO>> getAllNations() {
        return nationDAO.readAll();
    }

    public ApiResponse<NationDTO> updateNation(NationDTO nationDTO) {
        System.out.println("In update Nation");
        return nationDAO.update(nationDTO);
    }

    public ApiResponse<NationDTO> deleteNation(String id) {
        return nationDAO.delete(id);
    }
}
