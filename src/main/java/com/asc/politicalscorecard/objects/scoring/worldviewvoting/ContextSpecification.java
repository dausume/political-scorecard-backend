package com.asc.politicalscorecard.objects.scoring.worldviewvoting;

import com.asc.politicalscorecard.objects.scoring.terms.context.TermContextType;

/**
 * Defines a context requirement for a Worldview Election.
 * Specifies both the type of context (LOCATION, TIMEFRAME, etc.)
 * and the required granularity level (STATE, SINGLE_YEAR, etc.)
 *
 * This ensures all ballots in an election use comparable contexts.
 *
 * Example: "All ballots must use STATE-level location and SINGLE_YEAR timeframes"
 */
public class ContextSpecification {
    private String id;
    private TermContextType contextType;
    private String granularityLevel; // Depends on contextType

    public ContextSpecification() {
    }

    public ContextSpecification(TermContextType contextType, String granularityLevel) {
        this.contextType = contextType;
        this.granularityLevel = granularityLevel;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TermContextType getContextType() {
        return contextType;
    }

    public void setContextType(TermContextType contextType) {
        this.contextType = contextType;
    }

    public String getGranularityLevel() {
        return granularityLevel;
    }

    public void setGranularityLevel(String granularityLevel) {
        this.granularityLevel = granularityLevel;
    }

    @Override
    public String toString() {
        return "ContextSpecification{" +
                "contextType=" + contextType +
                ", granularityLevel='" + granularityLevel + '\'' +
                '}';
    }
}
