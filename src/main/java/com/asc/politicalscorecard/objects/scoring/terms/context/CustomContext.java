package com.asc.politicalscorecard.objects.scoring.terms.context;

import java.util.Map;

public class CustomContext extends TermContext {
    private String value;
    private Map<String, Object> metadata;

    public CustomContext() {
        super(TermContextType.CUSTOM, "");
    }

    public CustomContext(String label, String value, Map<String, Object> metadata) {
        super(TermContextType.CUSTOM, label);
        this.value = value;
        this.metadata = metadata;
    }

    @Override
    public String getValue() {
        return value;
    }

    // Getters and Setters
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
    public String toString() {
        return "CustomContext{" +
                "label='" + label + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
