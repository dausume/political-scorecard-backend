package com.asc.politicalscorecard.services;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.planetdaos.PlanetDAO;
import com.asc.politicalscorecard.json.dtos.planetdto.PlanetDTO;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PlanetService {

    private PlanetDAO planetDAO;

    public PlanetService(PlanetDAO planetDAO) {
        System.out.println("Initializing Planet Service");
        this.planetDAO = planetDAO;
    }

    public ApiResponse<PlanetDTO> createPlanet(PlanetDTO planet) {
        return planetDAO.create(planet);
    }

    public PlanetDTO getPlanet(String id) {
        return planetDAO.read(id);
    }

    public List<PlanetDTO> getAllPlanets() {
        return planetDAO.readAll();
    }

    public boolean updatePlanet(PlanetDTO planet) {
        return planetDAO.update(planet);
    }

    public boolean deletePlanet(String id) {
        return planetDAO.delete(id);
    }
        
}