package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.objects.scoring.terms.ValueMetadata;
import com.asc.politicalscorecard.objects.scoring.terms.ValueType;

/**
 * DTO for ValueMetadata.
 * Describes how to interpret and display a contextualized term's value.
 */
public class ValueMetadataDTO extends AbstractDTO {

    private ValueType type;
    private String unit;
    private String label;
    private Boolean isPositive; // true = positive indicator (higher is better), false = negative indicator (lower is better)

    // Constructors
    public ValueMetadataDTO() {
    }

    public ValueMetadataDTO(ValueType type, String unit, String label, Boolean isPositive) {
        this.type = type;
        this.unit = unit;
        this.label = label;
        this.isPositive = isPositive;
    }

    // Getters and Setters
    public ValueType getType() {
        return type;
    }

    public void setType(ValueType type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getIsPositive() {
        return isPositive;
    }

    public void setIsPositive(Boolean isPositive) {
        this.isPositive = isPositive;
    }

    @Override
    public ValueMetadata toEntity() {
        ValueMetadata metadata = new ValueMetadata();
        metadata.setType(this.type);
        metadata.setUnit(this.unit);
        metadata.setLabel(this.label);
        metadata.setIsPositive(this.isPositive);
        return metadata;
    }

    public static ValueMetadataDTO fromEntity(ValueMetadata metadata) {
        if (metadata == null) {
            return null;
        }
        ValueMetadataDTO dto = new ValueMetadataDTO();
        dto.setType(metadata.getType());
        dto.setUnit(metadata.getUnit());
        dto.setLabel(metadata.getLabel());
        dto.setIsPositive(metadata.getIsPositive());
        return dto;
    }

    @Override
    public String toString() {
        return "ValueMetadataDTO{" +
                "id='" + getId() + '\'' +
                ", type=" + type +
                ", unit='" + unit + '\'' +
                ", label='" + label + '\'' +
                ", isPositive=" + isPositive +
                '}';
    }
}
