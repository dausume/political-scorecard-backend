package com.asc.politicalscorecard.objects.scoring.worldviewvoting;

/**
 * Represents a score/weight assigned to a specific contextualized term in a worldview ballot.
 */
public class ContextualizedTermScore {
    // ID of the contextualized term being scored
    private String contextualizedTermId;

    // Weight assigned to this term (0.0 to 1.0 range)
    private double weight;

    // Constructors
    public ContextualizedTermScore() {
    }

    public ContextualizedTermScore(String contextualizedTermId, double weight) {
        this.contextualizedTermId = contextualizedTermId;
        this.weight = weight;
    }

    // Getters and Setters
    public String getContextualizedTermId() {
        return contextualizedTermId;
    }

    public void setContextualizedTermId(String contextualizedTermId) {
        this.contextualizedTermId = contextualizedTermId;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "ContextualizedTermScore{" +
                "termId='" + contextualizedTermId + '\'' +
                ", weight=" + weight +
                '}';
    }
}
