package com.asc.politicalscorecard.controllers.scoringcontrollers;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.legislation.LegislationAnnotationDTO;
import com.asc.politicalscorecard.services.scoringservices.LegislationAnnotationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/legislation/{legislationId}/annotations")
public class LegislationAnnotationController {

    private final LegislationAnnotationService annotationService;

    @Autowired
    public LegislationAnnotationController(LegislationAnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<LegislationAnnotationDTO>> createAnnotation(
            @PathVariable String legislationId,
            @RequestBody LegislationAnnotationDTO dto) {
        dto.setLegislationId(legislationId);
        ApiResponse<LegislationAnnotationDTO> response = annotationService.createAnnotation(dto);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<LegislationAnnotationDTO>>> getAnnotations(
            @PathVariable String legislationId,
            @RequestParam(required = false) String groupId) {
        ApiResponse<List<LegislationAnnotationDTO>> response;
        if (groupId != null && !groupId.isEmpty()) {
            response = annotationService.getAnnotationsByLegislationAndGroup(legislationId, groupId);
        } else {
            response = annotationService.getAnnotationsByLegislation(legislationId);
        }
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{annotationId}")
    public ResponseEntity<ApiResponse<LegislationAnnotationDTO>> updateAnnotation(
            @PathVariable String legislationId,
            @PathVariable String annotationId,
            @RequestBody LegislationAnnotationDTO dto) {
        dto.setId(annotationId);
        dto.setLegislationId(legislationId);
        ApiResponse<LegislationAnnotationDTO> response = annotationService.updateAnnotation(dto);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{annotationId}")
    public ResponseEntity<ApiResponse<LegislationAnnotationDTO>> deleteAnnotation(
            @PathVariable String legislationId,
            @PathVariable String annotationId) {
        ApiResponse<LegislationAnnotationDTO> response = annotationService.deleteAnnotation(annotationId);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
