package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.legislation.LegislationDTO;
import com.asc.politicalscorecard.services.scoringservices.LegislationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/legislation")
public class LegislationController {

    private final LegislationService legislationService;

    @Autowired
    public LegislationController(LegislationService legislationService) {
        this.legislationService = legislationService;
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<LegislationDTO>> createLegislation(@RequestBody LegislationDTO dto) {
        ApiResponse<LegislationDTO> response = legislationService.createLegislation(dto);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LegislationDTO>> getLegislationById(@PathVariable String id) {
        ApiResponse<LegislationDTO> response = legislationService.getLegislationById(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<LegislationDTO>>> getAllLegislations(
            @RequestParam(required = false) String status) {
        ApiResponse<List<LegislationDTO>> response;
        if (status != null && !status.isEmpty()) {
            response = legislationService.getLegislationsByStatus(status);
        } else {
            response = legislationService.getAllLegislations();
        }
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LegislationDTO>> updateLegislation(
            @PathVariable String id, @RequestBody LegislationDTO dto) {
        dto.setId(id);
        ApiResponse<LegislationDTO> response = legislationService.updateLegislation(dto);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<LegislationDTO>> deleteLegislation(@PathVariable String id) {
        ApiResponse<LegislationDTO> response = legislationService.deleteLegislation(id);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<LegislationDTO>> updateStatus(
            @PathVariable String id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isEmpty()) {
            return new ResponseEntity<>(
                new ApiResponse<>(false, "Status is required", null), HttpStatus.BAD_REQUEST);
        }
        ApiResponse<LegislationDTO> response = legislationService.updateStatus(id, newStatus);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @PathVariable String id, @RequestParam("file") MultipartFile file) {
        ApiResponse<String> response = legislationService.uploadDocument(id, file);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id, @PathVariable String filename) {
        try {
            InputStream stream = legislationService.downloadDocument(id, filename);
            byte[] bytes = stream.readAllBytes();
            stream.close();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportAsPdf(@PathVariable String id) {
        try {
            byte[] pdfBytes = legislationService.exportAsPdf(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "legislation-" + id + ".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/export/docx")
    public ResponseEntity<byte[]> exportAsDocx(@PathVariable String id) {
        try {
            byte[] docxBytes = legislationService.exportAsDocx(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDispositionFormData("attachment", "legislation-" + id + ".docx");
            return new ResponseEntity<>(docxBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/export/annotations/pdf")
    public ResponseEntity<byte[]> exportAnnotationsAsPdf(@PathVariable String id) {
        try {
            byte[] pdfBytes = legislationService.exportAnnotationsAsPdf(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "annotations-" + id + ".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/export/annotations/docx")
    public ResponseEntity<byte[]> exportAnnotationsAsDocx(@PathVariable String id) {
        try {
            byte[] docxBytes = legislationService.exportAnnotationsAsDocx(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
            headers.setContentDispositionFormData("attachment", "annotations-" + id + ".docx");
            return new ResponseEntity<>(docxBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
