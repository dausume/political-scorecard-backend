package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;

import java.util.List;

/**
 * DTO representing a complete hierarchy chain for a context.
 * Example: For "San Francisco" context, the chain might be:
 * [North America (Region), United States (Nation), California (State), San Francisco (City)]
 */
public class ContextHierarchyChainDTO {

    private String contextId;              // The context for which we're showing the hierarchy
    private TermContextType contextType;    // Type of context (LOCATION, TIMEFRAME, etc.)
    private String contextLabel;            // Label of the main context
    private List<ContextNode> ancestorChain; // Ordered list from least specific to most specific
    private List<ContextNode> descendantChain; // Ordered list of all descendants
    private int totalDepth;                 // Total depth in the hierarchy tree

    /**
     * Represents a single node in the hierarchy chain
     */
    public static class ContextNode {
        private String contextId;
        private String label;
        private TermContextType type;
        private int specificityLevel;  // 1 = least specific, higher = more specific
        private String value;          // The getValue() result from the context

        public ContextNode() {
        }

        public ContextNode(String contextId, String label, TermContextType type, int specificityLevel, String value) {
            this.contextId = contextId;
            this.label = label;
            this.type = type;
            this.specificityLevel = specificityLevel;
            this.value = value;
        }

        // Getters and Setters
        public String getContextId() {
            return contextId;
        }

        public void setContextId(String contextId) {
            this.contextId = contextId;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public TermContextType getType() {
            return type;
        }

        public void setType(TermContextType type) {
            this.type = type;
        }

        public int getSpecificityLevel() {
            return specificityLevel;
        }

        public void setSpecificityLevel(int specificityLevel) {
            this.specificityLevel = specificityLevel;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return label + " (Level " + specificityLevel + ")";
        }
    }

    // Constructors
    public ContextHierarchyChainDTO() {
    }

    public ContextHierarchyChainDTO(String contextId, TermContextType contextType, String contextLabel) {
        this.contextId = contextId;
        this.contextType = contextType;
        this.contextLabel = contextLabel;
    }

    // Getters and Setters
    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public TermContextType getContextType() {
        return contextType;
    }

    public void setContextType(TermContextType contextType) {
        this.contextType = contextType;
    }

    public String getContextLabel() {
        return contextLabel;
    }

    public void setContextLabel(String contextLabel) {
        this.contextLabel = contextLabel;
    }

    public List<ContextNode> getAncestorChain() {
        return ancestorChain;
    }

    public void setAncestorChain(List<ContextNode> ancestorChain) {
        this.ancestorChain = ancestorChain;
    }

    public List<ContextNode> getDescendantChain() {
        return descendantChain;
    }

    public void setDescendantChain(List<ContextNode> descendantChain) {
        this.descendantChain = descendantChain;
    }

    public int getTotalDepth() {
        return totalDepth;
    }

    public void setTotalDepth(int totalDepth) {
        this.totalDepth = totalDepth;
    }

    /**
     * Get a formatted hierarchy path string
     * Example: "North America > United States > California > San Francisco"
     */
    public String getFormattedPath() {
        if (ancestorChain == null || ancestorChain.isEmpty()) {
            return contextLabel;
        }

        StringBuilder path = new StringBuilder();
        for (ContextNode node : ancestorChain) {
            path.append(node.getLabel()).append(" > ");
        }
        path.append(contextLabel);

        return path.toString();
    }

    @Override
    public String toString() {
        return "ContextHierarchyChainDTO{" +
                "contextId='" + contextId + '\'' +
                ", contextType=" + contextType +
                ", contextLabel='" + contextLabel + '\'' +
                ", path='" + getFormattedPath() + '\'' +
                ", totalDepth=" + totalDepth +
                '}';
    }
}
