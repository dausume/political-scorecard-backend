package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.objects.scoring.terms.ContextualizedTerm;

import java.util.List;

/**
 * DTO for ContextualizedTerm.
 * Represents a term with specific contexts and normalization values.
 */
public class ContextualizedTermDTO extends AbstractDTO {

    private String termId;                          // Reference to base term
    private List<String> contextIds;                // References to contexts
    private String valueMetadataId;                 // Reference to value metadata
    private ValueMetadataDTO valueMetadata;         // Full value metadata object (for responses)
    private TermDTO term;                           // Full term object (for responses)
    private List<TermContextDTO> contexts;          // Full context objects (for responses)
    private Double preNormalizedValue;
    private Double postNormalizedValue;
    private String preProcessPolariUrl;
    private String postProcessPolariUrl;

    // Constructors
    public ContextualizedTermDTO() {
    }

    public ContextualizedTermDTO(String termId, List<String> contextIds, String valueMetadataId,
                                 Double preNormalizedValue, Double postNormalizedValue) {
        this.termId = termId;
        this.contextIds = contextIds;
        this.valueMetadataId = valueMetadataId;
        this.preNormalizedValue = preNormalizedValue;
        this.postNormalizedValue = postNormalizedValue;
    }

    // Getters and Setters
    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public List<String> getContextIds() {
        return contextIds;
    }

    public void setContextIds(List<String> contextIds) {
        this.contextIds = contextIds;
    }

    public String getValueMetadataId() {
        return valueMetadataId;
    }

    public void setValueMetadataId(String valueMetadataId) {
        this.valueMetadataId = valueMetadataId;
    }

    public ValueMetadataDTO getValueMetadata() {
        return valueMetadata;
    }

    public void setValueMetadata(ValueMetadataDTO valueMetadata) {
        this.valueMetadata = valueMetadata;
    }

    public TermDTO getTerm() {
        return term;
    }

    public void setTerm(TermDTO term) {
        this.term = term;
    }

    public List<TermContextDTO> getContexts() {
        return contexts;
    }

    public void setContexts(List<TermContextDTO> contexts) {
        this.contexts = contexts;
    }

    public Double getPreNormalizedValue() {
        return preNormalizedValue;
    }

    public void setPreNormalizedValue(Double preNormalizedValue) {
        this.preNormalizedValue = preNormalizedValue;
    }

    public Double getPostNormalizedValue() {
        return postNormalizedValue;
    }

    public void setPostNormalizedValue(Double postNormalizedValue) {
        this.postNormalizedValue = postNormalizedValue;
    }

    public String getPreProcessPolariUrl() {
        return preProcessPolariUrl;
    }

    public void setPreProcessPolariUrl(String preProcessPolariUrl) {
        this.preProcessPolariUrl = preProcessPolariUrl;
    }

    public String getPostProcessPolariUrl() {
        return postProcessPolariUrl;
    }

    public void setPostProcessPolariUrl(String postProcessPolariUrl) {
        this.postProcessPolariUrl = postProcessPolariUrl;
    }

    @Override
    public ContextualizedTerm toEntity() {
        ContextualizedTerm contextualizedTerm = new ContextualizedTerm();
        contextualizedTerm.setId(this.getId());
        contextualizedTerm.setPreNormalizedValue(this.preNormalizedValue != null ? this.preNormalizedValue : 0.0);
        contextualizedTerm.setPostNormalizedValue(this.postNormalizedValue != null ? this.postNormalizedValue : 0.0);
        contextualizedTerm.setPreProcessPolariUrl(this.preProcessPolariUrl);
        contextualizedTerm.setPostProcessPolariUrl(this.postProcessPolariUrl);

        // Note: term, contexts, and valueMetadata need to be set separately
        // as they require additional database lookups
        return contextualizedTerm;
    }

    public static ContextualizedTermDTO fromEntity(ContextualizedTerm entity) {
        if (entity == null) {
            return null;
        }
        ContextualizedTermDTO dto = new ContextualizedTermDTO();
        dto.setId(entity.getId());
        dto.setPreNormalizedValue(entity.getPreNormalizedValue());
        dto.setPostNormalizedValue(entity.getPostNormalizedValue());
        dto.setPreProcessPolariUrl(entity.getPreProcessPolariUrl());
        dto.setPostProcessPolariUrl(entity.getPostProcessPolariUrl());

        // Set references if available
        if (entity.getTerm() != null) {
            dto.setTermId(entity.getTerm().getId());
        }

        return dto;
    }

    @Override
    public String toString() {
        return "ContextualizedTermDTO{" +
                "id='" + getId() + '\'' +
                ", termId='" + termId + '\'' +
                ", contextIds=" + (contextIds != null ? contextIds.size() : 0) +
                ", preNormalizedValue=" + preNormalizedValue +
                ", postNormalizedValue=" + postNormalizedValue +
                '}';
    }
}
