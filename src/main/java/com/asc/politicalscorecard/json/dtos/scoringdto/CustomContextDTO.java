package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.objects.scoring.terms.context.CustomContext;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for Custom Context.
 * Flexible context type that can store any key-value metadata.
 */
public class CustomContextDTO extends TermContextDTO {

    private String value;
    private Map<String, Object> metadata;

    // Constructors
    public CustomContextDTO() {
        super(TermContextType.CUSTOM, "");
        this.metadata = new HashMap<>();
    }

    public CustomContextDTO(String label, String value, Map<String, Object> metadata) {
        super(TermContextType.CUSTOM, label);
        this.value = value;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    // Getters and Setters
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public CustomContext toEntity() {
        CustomContext context = new CustomContext();
        context.setId(this.getId());
        context.setLabel(this.getLabel());
        context.setValue(this.value);
        context.setMetadata(this.metadata);
        return context;
    }

    public static CustomContextDTO fromEntity(CustomContext context) {
        if (context == null) {
            return null;
        }
        CustomContextDTO dto = new CustomContextDTO();
        dto.setId(context.getId());
        dto.setLabel(context.getLabel());
        dto.setValue(context.getValue());
        dto.setMetadata(context.getMetadata());
        return dto;
    }

    @Override
    public String toString() {
        return "CustomContextDTO{" +
                "id='" + getId() + '\'' +
                ", label='" + getLabel() + '\'' +
                ", value='" + value + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
