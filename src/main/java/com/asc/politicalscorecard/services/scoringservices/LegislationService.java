package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.LegislationDAO;
import com.asc.politicalscorecard.json.dtos.legislation.LegislationAnnotationDTO;
import com.asc.politicalscorecard.json.dtos.legislation.LegislationDTO;
import com.asc.politicalscorecard.services.documentservices.DocumentConversionService;
import com.asc.politicalscorecard.services.storage.MinioStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
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
    private final LegislationAnnotationService annotationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public LegislationService(LegislationDAO legislationDAO, MinioStorageService minioStorageService,
                              DocumentConversionService documentConversionService,
                              LegislationAnnotationService annotationService) {
        this.legislationDAO = legislationDAO;
        this.minioStorageService = minioStorageService;
        this.documentConversionService = documentConversionService;
        this.annotationService = annotationService;
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

    // --- Annotation export ---

    public byte[] exportAnnotationsAsPdf(String id) {
        String html = buildAnnotationsHtml(id);
        return documentConversionService.htmlToPdf(html);
    }

    public byte[] exportAnnotationsAsDocx(String id) {
        String html = buildAnnotationsHtml(id);
        return documentConversionService.htmlToDocx(html);
    }

    private String buildAnnotationsHtml(String legislationId) {
        // Fetch legislation for title
        ApiResponse<LegislationDTO> legResponse = legislationDAO.read(legislationId);
        String title = "Unknown Legislation";
        if (legResponse.isSuccess() && legResponse.getData() != null) {
            title = legResponse.getData().getTitle();
        }

        // Fetch annotations
        ApiResponse<List<LegislationAnnotationDTO>> annResponse =
                annotationService.getAnnotationsByLegislation(legislationId);
        List<LegislationAnnotationDTO> annotations =
                (annResponse.isSuccess() && annResponse.getData() != null)
                        ? annResponse.getData() : Collections.emptyList();

        // Group annotations by selection (exact text from targetJson)
        Map<String, List<LegislationAnnotationDTO>> selectionMap = new LinkedHashMap<>();
        for (LegislationAnnotationDTO ann : annotations) {
            String exact = extractExactText(ann.getTargetJson());
            selectionMap.computeIfAbsent(exact, k -> new ArrayList<>()).add(ann);
        }

        // Build HTML
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Annotations Report</h1>");
        sb.append("<p style=\"color:#666;\">Legislation: ").append(escapeHtml(title)).append("</p>");

        if (selectionMap.isEmpty()) {
            sb.append("<p>No annotations found.</p>");
            return sb.toString();
        }

        int selectionIndex = 1;
        for (Map.Entry<String, List<LegislationAnnotationDTO>> entry : selectionMap.entrySet()) {
            String exact = entry.getKey();
            List<LegislationAnnotationDTO> anns = entry.getValue();

            sb.append("<div style=\"margin-bottom:20px;\">");
            sb.append("<h2 style=\"font-size:14pt;\">Selection ").append(selectionIndex++)
              .append(": &ldquo;").append(escapeHtml(exact)).append("&rdquo;</h2>");

            for (LegislationAnnotationDTO ann : anns) {
                sb.append("<div style=\"margin-left:20px;margin-bottom:10px;padding:8px;border-left:3px solid #ccc;\">");
                sb.append("<p style=\"margin:0 0 4px;\"><strong>")
                  .append(escapeHtml(ann.getAnnotationType())).append("</strong></p>");
                sb.append(formatAnnotationBody(ann.getAnnotationType(), ann.getBodyJson()));
                sb.append("</div>");
            }

            sb.append("</div>");
        }

        return sb.toString();
    }

    private String extractExactText(String targetJson) {
        try {
            JsonNode root = objectMapper.readTree(targetJson);
            JsonNode exact = root.path("selector").path("exact");
            return exact.isMissingNode() ? "(unknown)" : exact.asText();
        } catch (Exception e) {
            return "(unknown)";
        }
    }

    private String formatAnnotationBody(String type, String bodyJson) {
        try {
            JsonNode body = objectMapper.readTree(bodyJson);
            StringBuilder sb = new StringBuilder();
            switch (type) {
                case "INTENT":
                    sb.append("<p style=\"margin:2px 0;\">Category: ")
                      .append(escapeHtml(body.path("categoryName").asText("N/A"))).append("</p>");
                    sb.append("<p style=\"margin:2px 0;\">Group: ")
                      .append(escapeHtml(body.path("groupName").asText("N/A"))).append("</p>");
                    sb.append("<p style=\"margin:2px 0;\">Sentiment: ")
                      .append(escapeHtml(body.path("sentiment").asText("N/A"))).append("</p>");
                    break;
                case "SCORING":
                    String electionName = body.path("electionName").asText("");
                    if (!electionName.isEmpty()) {
                        sb.append("<p style=\"margin:2px 0;\">Election: ")
                          .append(escapeHtml(electionName)).append("</p>");
                    }
                    String ballotId = body.path("ballotId").asText("");
                    if (!ballotId.isEmpty()) {
                        sb.append("<p style=\"margin:2px 0;\">Ballot: ")
                          .append(escapeHtml(ballotId)).append("</p>");
                    }
                    break;
                case "SOLUTION":
                    sb.append("<p style=\"margin:2px 0;\">Title: ")
                      .append(escapeHtml(body.path("title").asText("N/A"))).append("</p>");
                    String desc = body.path("description").asText("");
                    if (!desc.isEmpty()) {
                        sb.append("<p style=\"margin:2px 0;\">Description: ")
                          .append(escapeHtml(desc)).append("</p>");
                    }
                    break;
                default:
                    sb.append("<p style=\"margin:2px 0;\">").append(escapeHtml(bodyJson)).append("</p>");
            }
            return sb.toString();
        } catch (Exception e) {
            return "<p>" + escapeHtml(bodyJson) + "</p>";
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}
