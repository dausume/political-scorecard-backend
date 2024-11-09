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

    public ApiResponse<PlanetDTO> getPlanet(String id) {
        return planetDAO.read(id);
    }

    public ApiResponse<List<PlanetDTO>> getAllPlanets() {
        return planetDAO.readAll();
    }

    public ApiResponse<PlanetDTO> updatePlanet(PlanetDTO planet) {
        return planetDAO.update(planet);
    }

    public ApiResponse<PlanetDTO> deletePlanet(String id) {
        return planetDAO.delete(id);
    }
        
}