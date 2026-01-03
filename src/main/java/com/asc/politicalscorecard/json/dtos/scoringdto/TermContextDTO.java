package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContext;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base DTO for TermContext with polymorphic support.
 * Supports different context types: Timeframe, Location, Demographic, Economic, Custom.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TimeframeContextDTO.class, name = "TIMEFRAME"),
    @JsonSubTypes.Type(value = LocationContextDTO.class, name = "LOCATION"),
    @JsonSubTypes.Type(value = DemographicContextDTO.class, name = "DEMOGRAPHIC"),
    @JsonSubTypes.Type(value = EconomicContextDTO.class, name = "ECONOMIC"),
    @JsonSubTypes.Type(value = CustomContextDTO.class, name = "CUSTOM")
})
public abstract class TermContextDTO extends AbstractDTO {

    private TermContextType type;
    private String label;

    // Constructors
    public TermContextDTO() {
    }

    public TermContextDTO(TermContextType type, String label) {
        this.type = type;
        this.label = label;
    }

    // Getters and Setters
    public TermContextType getType() {
        return type;
    }

    public void setType(TermContextType type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    // Abstract method for getting value representation
    public abstract String getValue();

    @Override
    public abstract TermContext toEntity();

    @Override
    public String toString() {
        return "TermContextDTO{" +
                "id='" + getId() + '\'' +
                ", type=" + type +
                ", label='" + label + '\'' +
                '}';
    }
}
