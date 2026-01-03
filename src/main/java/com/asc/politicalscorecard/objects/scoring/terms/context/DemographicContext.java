package com.asc.politicalscorecard.objects.scoring.terms.context;

import java.util.ArrayList;
import java.util.List;

public class DemographicContext extends TermContext {
    private String ageRange;
    private String incomeLevel;
    private String education;
    private String occupation;

    public DemographicContext() {
        super(TermContextType.DEMOGRAPHIC, "");
    }

    public DemographicContext(String label, String ageRange, String incomeLevel, String education, String occupation) {
        super(TermContextType.DEMOGRAPHIC, label);
        this.ageRange = ageRange;
        this.incomeLevel = incomeLevel;
        this.education = education;
        this.occupation = occupation;
    }

    @Override
    public String getValue() {
        List<String> parts = new ArrayList<>();
        if (ageRange != null && !ageRange.isEmpty()) parts.add(ageRange);
        if (incomeLevel != null && !incomeLevel.isEmpty()) parts.add(incomeLevel);
        if (education != null && !education.isEmpty()) parts.add(education);
        if (occupation != null && !occupation.isEmpty()) parts.add(occupation);
        return String.join(", ", parts);
    }

    // Getters and Setters
    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public String getIncomeLevel() {
        return incomeLevel;
    }

    public void setIncomeLevel(String incomeLevel) {
        this.incomeLevel = incomeLevel;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    @Override
    public String toString() {
        return "DemographicContext{" +
                "label='" + label + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }
}
