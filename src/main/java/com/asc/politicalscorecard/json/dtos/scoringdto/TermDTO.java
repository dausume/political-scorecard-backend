package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.objects.scoring.terms.Term;

/**
 * Data Transfer Object for Term.
 * Used for API communication and data transfer.
 */
public class TermDTO extends AbstractDTO {

    private String name;
    private String description;
    private String source;
    private String category;

    // Constructors
    public TermDTO() {
    }

    public TermDTO(String id, String name, String description, String source, String category) {
        this.setId(id);
        this.name = name;
        this.description = description;
        this.source = source;
        this.category = category;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Convert DTO to Entity
    @Override
    public Term toEntity() {
        Term term = new Term();
        term.setId(this.getId());
        term.setName(this.name);
        term.setDescription(this.description);
        term.setSource(this.source);
        term.setCategory(this.category);
        return term;
    }

    // Convert Entity to DTO
    public static TermDTO fromEntity(Term term) {
        if (term == null) {
            return null;
        }
        TermDTO dto = new TermDTO();
        dto.setId(term.getId());
        dto.setName(term.getName());
        dto.setDescription(term.getDescription());
        dto.setSource(term.getSource());
        dto.setCategory(term.getCategory());
        return dto;
    }

    @Override
    public String toString() {
        return "TermDTO{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
