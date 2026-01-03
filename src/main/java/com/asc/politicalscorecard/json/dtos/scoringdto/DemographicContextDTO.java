package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.objects.scoring.terms.context.DemographicContext;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;

/**
 * DTO for Demographic Context.
 * Supports demographic segmentation by age, income, education, occupation.
 */
public class DemographicContextDTO extends TermContextDTO {

    private String ageRange;
    private String incomeLevel;
    private String education;
    private String occupation;

    // Constructors
    public DemographicContextDTO() {
        super(TermContextType.DEMOGRAPHIC, "");
    }

    public DemographicContextDTO(String label, String ageRange, String incomeLevel, String education, String occupation) {
        super(TermContextType.DEMOGRAPHIC, label);
        this.ageRange = ageRange;
        this.incomeLevel = incomeLevel;
        this.education = education;
        this.occupation = occupation;
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
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        if (ageRange != null) sb.append(ageRange);
        if (incomeLevel != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(incomeLevel);
        }
        if (education != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(education);
        }
        if (occupation != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(occupation);
        }
        return sb.toString();
    }

    @Override
    public DemographicContext toEntity() {
        DemographicContext context = new DemographicContext();
        context.setId(this.getId());
        context.setLabel(this.getLabel());
        context.setAgeRange(this.ageRange);
        context.setIncomeLevel(this.incomeLevel);
        context.setEducation(this.education);
        context.setOccupation(this.occupation);
        return context;
    }

    public static DemographicContextDTO fromEntity(DemographicContext context) {
        if (context == null) {
            return null;
        }
        DemographicContextDTO dto = new DemographicContextDTO();
        dto.setId(context.getId());
        dto.setLabel(context.getLabel());
        dto.setAgeRange(context.getAgeRange());
        dto.setIncomeLevel(context.getIncomeLevel());
        dto.setEducation(context.getEducation());
        dto.setOccupation(context.getOccupation());
        return dto;
    }

    @Override
    public String toString() {
        return "DemographicContextDTO{" +
                "id='" + getId() + '\'' +
                ", label='" + getLabel() + '\'' +
                ", ageRange='" + ageRange + '\'' +
                ", incomeLevel='" + incomeLevel + '\'' +
                ", education='" + education + '\'' +
                ", occupation='" + occupation + '\'' +
                '}';
    }
}
