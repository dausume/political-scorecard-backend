package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.ValueMetadataDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.ValueMetadataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for ValueMetadata operations.
 */
@Service
public class ValueMetadataService {

    private final ValueMetadataDAO valueMetadataDAO;

    @Autowired
    public ValueMetadataService(ValueMetadataDAO valueMetadataDAO) {
        this.valueMetadataDAO = valueMetadataDAO;
    }

    public ApiResponse<ValueMetadataDTO> createValueMetadata(ValueMetadataDTO dto) {
        return valueMetadataDAO.create(dto);
    }

    public ApiResponse<ValueMetadataDTO> getValueMetadataById(String id) {
        return valueMetadataDAO.read(id);
    }

    public ApiResponse<List<ValueMetadataDTO>> getAllValueMetadata() {
        return valueMetadataDAO.readAll();
    }

    public ApiResponse<ValueMetadataDTO> updateValueMetadata(ValueMetadataDTO dto) {
        return valueMetadataDAO.update(dto);
    }

    public ApiResponse<ValueMetadataDTO> deleteValueMetadata(String id) {
        return valueMetadataDAO.delete(id);
    }
}
