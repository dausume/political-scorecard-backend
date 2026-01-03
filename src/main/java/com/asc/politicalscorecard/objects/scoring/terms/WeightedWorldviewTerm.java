package com.asc.politicalscorecard.objects.scoring.terms;

/**
 * Represents a term with user-assigned weight for a specific worldview ballot.
 * This tracks how much importance a user gives to this term in their scoring.
 */
public class WeightedWorldviewTerm {
    // Unique identifier for this weighted term
    private String id;

    // Reference to the base term
    private Term term;

    // Weight assigned by user (0.0 to 1.0 scale)
    private double weight;

    // ID of the worldview ballot this weighted term belongs to
    private String worldviewBallotId;

    // Whether this term contributes positively or negatively to the score
    private boolean isPositive;

    // Constructors
    public WeightedWorldviewTerm() {
    }

    public WeightedWorldviewTerm(String id, Term term, double weight, String worldviewBallotId, boolean isPositive) {
        this.id = id;
        this.term = term;
        this.weight = Math.max(0.0, Math.min(1.0, weight)); // Ensure weight is in 0-1 range
        this.worldviewBallotId = worldviewBallotId;
        this.isPositive = isPositive;
    }

    // Helper methods

    /**
     * Update the weight for this term
     * @param newWeight Value between 0.0 and 1.0
     */
    public void updateWeight(double newWeight) {
        this.weight = Math.max(0.0, Math.min(1.0, newWeight));
    }

    /**
     * Get weight as a percentage (0-100)
     */
    public int getWeightPercentage() {
        return (int) Math.round(weight * 100);
    }

    /**
     * Set weight from percentage (0-100)
     */
    public void setWeightFromPercentage(int percentage) {
        this.weight = Math.max(0.0, Math.min(100.0, percentage)) / 100.0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = Math.max(0.0, Math.min(1.0, weight));
    }

    public String getWorldviewBallotId() {
        return worldviewBallotId;
    }

    public void setWorldviewBallotId(String worldviewBallotId) {
        this.worldviewBallotId = worldviewBallotId;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    @Override
    public String toString() {
        return "WeightedWorldviewTerm{" +
                "id='" + id + '\'' +
                ", term=" + (term != null ? term.getName() : "null") +
                ", weight=" + String.format("%.2f", weight) +
                ", ballot='" + worldviewBallotId + '\'' +
                ", " + (isPositive ? "positive" : "negative") +
                '}';
    }
}
