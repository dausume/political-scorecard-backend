package com.asc.politicalscorecard.services.nationgeolocationservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.nationgeodaos.NationGeoLocationDAO;
import com.asc.politicalscorecard.json.dtos.nationgeolocationdto.NationGeoLocationDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NationGeoLocationService {

    private final NationGeoLocationDAO nationGeoLocationDAO;

    @Autowired
    public NationGeoLocationService(NationGeoLocationDAO nationGeoLocationDAO) {
        this.nationGeoLocationDAO = nationGeoLocationDAO;
    }

    public ApiResponse<NationGeoLocationDTO> createNationGeoLocation(NationGeoLocationDTO nationGeoLocationDTO) {
        return nationGeoLocationDAO.create(nationGeoLocationDTO);
    }

    public ApiResponse<NationGeoLocationDTO> getNationGeoLocationById(String id) {
        return nationGeoLocationDAO.read(id);
    }

    public ApiResponse<List<NationGeoLocationDTO>> getAllNationGeoLocations() {
        return nationGeoLocationDAO.readAll();
    }

    public ApiResponse<NationGeoLocationDTO> updateNationGeoLocation(NationGeoLocationDTO nationGeoLocationDTO) {
        System.out.println("In update NationGeoLocation");
        return nationGeoLocationDAO.update(nationGeoLocationDTO);
    }

    public ApiResponse<NationGeoLocationDTO> deleteNationGeoLocation(String id) {
        return nationGeoLocationDAO.delete(id);
    }
}
