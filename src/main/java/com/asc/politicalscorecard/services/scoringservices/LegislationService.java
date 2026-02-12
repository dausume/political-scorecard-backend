package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.LegislationDAO;
import com.asc.politicalscorecard.json.dtos.legislation.LegislationDTO;
import com.asc.politicalscorecard.services.documentservices.DocumentConversionService;
import com.asc.politicalscorecard.services.storage.MinioStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class LegislationService {

    private static final Logger logger = Logger.getLogger(LegislationService.class.getName());

    private static final Set<String> VALID_TRANSITIONS = Set.of(
        "DRAFT->SUBMITTED", "SUBMITTED->APPROVED", "APPROVED->LOCKED",
        "SUBMITTED->DRAFT", "APPROVED->DRAFT"
    );

    private final LegislationDAO legislationDAO;
    private final MinioStorageService minioStorageService;
    private final DocumentConversionService documentConversionService;

    @Autowired
    public LegislationService(LegislationDAO legislationDAO, MinioStorageService minioStorageService,
                              DocumentConversionService documentConversionService) {
        this.legislationDAO = legislationDAO;
        this.minioStorageService = minioStorageService;
        this.documentConversionService = documentConversionService;
    }

    public ApiResponse<LegislationDTO> createLegislation(LegislationDTO dto) {
        return legislationDAO.create(dto);
    }

    public ApiResponse<LegislationDTO> getLegislationById(String id) {
        return legislationDAO.read(id);
    }

    public ApiResponse<List<LegislationDTO>> getAllLegislations() {
        return legislationDAO.readAll();
    }

    public ApiResponse<List<LegislationDTO>> getLegislationsByStatus(String status) {
        return legislationDAO.readByStatus(status);
    }

    public ApiResponse<LegislationDTO> updateLegislation(LegislationDTO dto) {
        return legislationDAO.update(dto);
    }

    public ApiResponse<LegislationDTO> deleteLegislation(String id) {
        return legislationDAO.delete(id);
    }

    public ApiResponse<LegislationDTO> updateStatus(String id, String newStatus) {
        ApiResponse<LegislationDTO> current = legislationDAO.read(id);
        if (!current.isSuccess()) {
            return new ApiResponse<>(false, "Legislation not found", null);
        }

        String currentStatus = current.getData().getStatus();
        String transition = currentStatus + "->" + newStatus;

        if (!VALID_TRANSITIONS.contains(transition)) {
            return new ApiResponse<>(false,
                "Invalid status transition: " + currentStatus + " -> " + newStatus, null);
        }

        return legislationDAO.updateStatus(id, newStatus);
    }

    public ApiResponse<String> uploadDocument(String id, MultipartFile file) {
        try {
            String objectKey = id + "/" + file.getOriginalFilename();
            minioStorageService.uploadFile(objectKey, file.getInputStream(), file.getContentType());
            return new ApiResponse<>(true, "File uploaded successfully", objectKey);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error uploading document: ", e);
            return new ApiResponse<>(false, "Error uploading document: " + e.getMessage(), null);
        }
    }

    public InputStream downloadDocument(String id, String filename) {
        String objectKey = id + "/" + filename;
        return minioStorageService.downloadFile(objectKey);
    }

    public byte[] exportAsPdf(String id) {
        ApiResponse<LegislationDTO> response = legislationDAO.read(id);
        if (!response.isSuccess() || response.getData() == null) {
            throw new RuntimeException("Legislation not found for export");
        }
        String html = response.getData().getLegislationText();
        if (html == null || html.isEmpty()) {
            throw new RuntimeException("Legislation has no text content to export");
        }
        return documentConversionService.htmlToPdf(html);
    }

    public byte[] exportAsDocx(String id) {
        ApiResponse<LegislationDTO> response = legislationDAO.read(id);
        if (!response.isSuccess() || response.getData() == null) {
            throw new RuntimeException("Legislation not found for export");
        }
        String html = response.getData().getLegislationText();
        if (html == null || html.isEmpty()) {
            throw new RuntimeException("Legislation has no text content to export");
        }
        return documentConversionService.htmlToDocx(html);
    }
}
