package com.asc.politicalscorecard.services.nationservices;

import com.asc.politicalscorecard.databases.daos.nationdaos.NationWithPlanetDAO;
import com.asc.politicalscorecard.json.dtos.nationdto.NationWithPlanetDTO;
import org.springframework.stereotype.Service;

@Service
public class NationWithPlanetService {

    private final NationWithPlanetDAO nationWithPlanetDAO;

    public NationWithPlanetService(NationWithPlanetDAO nationWithPlanetDAO) {
        this.nationWithPlanetDAO = nationWithPlanetDAO;
    }

    public NationWithPlanetDTO getNationWithPlanetById(String id) {
        return nationWithPlanetDAO.findById(id);
    }
}