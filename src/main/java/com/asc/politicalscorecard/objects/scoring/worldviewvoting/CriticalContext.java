package com.asc.politicalscorecard.objects.scoring.worldviewvoting;

import com.asc.politicalscorecard.objects.scoring.terms.context.TermContext;

import java.util.List;

/**
 * Represents a context that is foundationally impacted significantly and differentially
 * from general scoring, suggested by the system for the user to consider.
 */
public class CriticalContext {
    // Unique identifier
    private String id;

    // Name of the critical context
    private String name;

    // Description of why this context is critical
    private String description;

    // Different variations of this context for comparison
    private List<TermContext> contextVariations;

    // Constructors
    public CriticalContext() {
    }

    public CriticalContext(String id, String name, String description, List<TermContext> contextVariations) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.contextVariations = contextVariations;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<TermContext> getContextVariations() {
        return contextVariations;
    }

    public void setContextVariations(List<TermContext> contextVariations) {
        this.contextVariations = contextVariations;
    }

    @Override
    public String toString() {
        return "CriticalContext{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", variations=" + (contextVariations != null ? contextVariations.size() : 0) +
                '}';
    }
}
