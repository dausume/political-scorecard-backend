package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.LegislationAnnotationDAO;
import com.asc.politicalscorecard.json.dtos.legislation.LegislationAnnotationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LegislationAnnotationService {

    private final LegislationAnnotationDAO annotationDAO;

    @Autowired
    public LegislationAnnotationService(LegislationAnnotationDAO annotationDAO) {
        this.annotationDAO = annotationDAO;
    }

    public ApiResponse<LegislationAnnotationDTO> createAnnotation(LegislationAnnotationDTO dto) {
        return annotationDAO.create(dto);
    }

    public ApiResponse<LegislationAnnotationDTO> getAnnotationById(String id) {
        return annotationDAO.read(id);
    }

    public ApiResponse<List<LegislationAnnotationDTO>> getAnnotationsByLegislation(String legislationId) {
        return annotationDAO.readByLegislation(legislationId);
    }

    public ApiResponse<List<LegislationAnnotationDTO>> getAnnotationsByLegislationAndGroup(String legislationId, String groupId) {
        return annotationDAO.readByLegislationAndGroup(legislationId, groupId);
    }

    public ApiResponse<LegislationAnnotationDTO> updateAnnotation(LegislationAnnotationDTO dto) {
        return annotationDAO.update(dto);
    }

    public ApiResponse<LegislationAnnotationDTO> deleteAnnotation(String id) {
        return annotationDAO.delete(id);
    }
}
