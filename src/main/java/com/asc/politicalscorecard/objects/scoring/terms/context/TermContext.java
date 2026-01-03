package com.asc.politicalscorecard.objects.scoring.terms.context;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TimeframeContext.class, name = "TIMEFRAME"),
    @JsonSubTypes.Type(value = LocationContext.class, name = "LOCATION"),
    @JsonSubTypes.Type(value = DemographicContext.class, name = "DEMOGRAPHIC"),
    @JsonSubTypes.Type(value = EconomicContext.class, name = "ECONOMIC"),
    @JsonSubTypes.Type(value = CustomContext.class, name = "CUSTOM")
})
public abstract class TermContext {
    protected String id;
    protected TermContextType type;
    protected String label;

    public TermContext() {
    }

    public TermContext(TermContextType type, String label) {
        this.type = type;
        this.label = label;
    }

    public abstract String getValue();

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
}
