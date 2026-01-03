package com.asc.politicalscorecard.objects.scoring.terms;

public class ValueMetadata {
    private ValueType type;
    private String unit;
    private String label;
    private Boolean isPositive;

    public ValueMetadata() {
    }

    public ValueMetadata(ValueType type, String unit, String label, Boolean isPositive) {
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
    public String toString() {
        return "ValueMetadata{" +
                "type=" + type +
                ", unit='" + unit + '\'' +
                ", label='" + label + '\'' +
                ", isPositive=" + isPositive +
                '}';
    }
}
