package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;

/**
 * DTO for Context Hierarchy.
 * Tracks specificity relationships between contexts.
 * Example: "California" (state) is more specific than "USA" (nation)
 * Example: "2024 Q1" is more specific than "2024"
 */
public class ContextHierarchyDTO extends AbstractDTO {

    private String parentContextId;   // Less specific context
    private String childContextId;    // More specific context
    private TermContextType contextType;
    private int specificityDifference; // How many levels more specific (typically 1)
    private String hierarchyPath;      // E.g., "region > nation > state > city"

    // Constructors
    public ContextHierarchyDTO() {
    }

    public ContextHierarchyDTO(String parentContextId, String childContextId,
                               TermContextType contextType, int specificityDifference) {
        this.parentContextId = parentContextId;
        this.childContextId = childContextId;
        this.contextType = contextType;
        this.specificityDifference = specificityDifference;
    }

    // Getters and Setters
    public String getParentContextId() {
        return parentContextId;
    }

    public void setParentContextId(String parentContextId) {
        this.parentContextId = parentContextId;
    }

    public String getChildContextId() {
        return childContextId;
    }

    public void setChildContextId(String childContextId) {
        this.childContextId = childContextId;
    }

    public TermContextType getContextType() {
        return contextType;
    }

    public void setContextType(TermContextType contextType) {
        this.contextType = contextType;
    }

    public int getSpecificityDifference() {
        return specificityDifference;
    }

    public void setSpecificityDifference(int specificityDifference) {
        this.specificityDifference = specificityDifference;
    }

    public String getHierarchyPath() {
        return hierarchyPath;
    }

    public void setHierarchyPath(String hierarchyPath) {
        this.hierarchyPath = hierarchyPath;
    }

    @Override
    public Object toEntity() {
        // For now, this is primarily a DTO-only concept
        // Could create a ContextHierarchy entity if needed
        return null;
    }

    @Override
    public String toString() {
        return "ContextHierarchyDTO{" +
                "id='" + getId() + '\'' +
                ", parentContextId='" + parentContextId + '\'' +
                ", childContextId='" + childContextId + '\'' +
                ", contextType=" + contextType +
                ", specificityDifference=" + specificityDifference +
                ", hierarchyPath='" + hierarchyPath + '\'' +
                '}';
    }
}
