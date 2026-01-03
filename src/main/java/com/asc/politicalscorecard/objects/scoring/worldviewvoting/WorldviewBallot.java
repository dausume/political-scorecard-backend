package com.asc.politicalscorecard.objects.scoring.worldviewvoting;

import com.asc.politicalscorecard.objects.scoring.competitors.CompetitorDimension;
import com.asc.politicalscorecard.objects.scoring.terms.Term;
import com.asc.politicalscorecard.objects.scoring.terms.context.TermContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A worldview ballot is a contextualized ballot that allows users to score competitors
 * based on their personal worldview contexts and weighted term preferences.
 * This combines the template (available terms, competitors) with user-specific data.
 */
public class WorldviewBallot {
    // Unique identifier for this worldview ballot
    private String id;

    // ID of the worldview election this ballot belongs to
    private String electionId;

    // ID of the competitive score this ballot is associated with
    private String competitiveScoreId;

    // ID of the voter/user who owns this ballot
    private String voterId;

    // Name of the worldview ballot
    private String name;

    // Optional ballot type/category (e.g., 'labor-quality', 'economic')
    private String ballotType;

    // User-defined contexts for this worldview ballot (rich context objects)
    private List<TermContext> personalContexts;

    // Scores assigned to specific contextualized terms
    private List<ContextualizedTermScore> contextualizedTermScores;

    // Critical contexts suggested by the system for user consideration
    private List<CriticalContext> criticalContexts;

    // Legacy fields (for backward compatibility and future features)
    // The id of the user that created the worldview ballot (alias for voterId)
    private String userId;

    // The id of the category that the worldview ballot is associated with (alias for ballotType)
    private String categoryId;

    // The terms that are part of the worldview ballot template
    private Term[] terms;

    // The competitors that are part of the worldview ballot
    private CompetitorDimension[] competitors;

    // The contexts that are part of the worldview ballot (as IDs for backward compatibility)
    private String[] contexts;

    // Constructors
    public WorldviewBallot() {
    }

    public WorldviewBallot(String id, String electionId, String competitiveScoreId, String voterId, String name,
                          String ballotType, List<TermContext> personalContexts,
                          List<ContextualizedTermScore> contextualizedTermScores,
                          List<CriticalContext> criticalContexts) {
        this.id = id;
        this.electionId = electionId;
        this.competitiveScoreId = competitiveScoreId;
        this.voterId = voterId;
        this.userId = voterId; // Keep in sync
        this.name = name;
        this.ballotType = ballotType;
        this.categoryId = ballotType; // Keep in sync
        this.personalContexts = personalContexts;
        this.contextualizedTermScores = contextualizedTermScores;
        this.criticalContexts = criticalContexts;
    }

    // Helper methods

    /**
     * Get the weight for a specific contextualized term
     */
    public double getContextualizedTermWeight(String contextualizedTermId) {
        return contextualizedTermScores.stream()
                .filter(cts -> cts.getContextualizedTermId().equals(contextualizedTermId))
                .map(ContextualizedTermScore::getWeight)
                .findFirst()
                .orElse(0.0);
    }

    /**
     * Get personal context summary
     */
    public String getPersonalContextSummary() {
        return personalContexts.stream()
                .map(c -> c.getLabel() + ": " + c.getValue())
                .reduce((a, b) -> a + " | " + b)
                .orElse("");
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getElectionId() {
        return electionId;
    }

    public void setElectionId(String electionId) {
        this.electionId = electionId;
    }

    public String getCompetitiveScoreId() {
        return competitiveScoreId;
    }

    public void setCompetitiveScoreId(String competitiveScoreId) {
        this.competitiveScoreId = competitiveScoreId;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
        this.userId = voterId; // Keep in sync
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBallotType() {
        return ballotType;
    }

    public void setBallotType(String ballotType) {
        this.ballotType = ballotType;
        this.categoryId = ballotType; // Keep in sync
    }

    public List<TermContext> getPersonalContexts() {
        return personalContexts;
    }

    public void setPersonalContexts(List<TermContext> personalContexts) {
        this.personalContexts = personalContexts;
    }

    public List<ContextualizedTermScore> getContextualizedTermScores() {
        return contextualizedTermScores;
    }

    public void setContextualizedTermScores(List<ContextualizedTermScore> contextualizedTermScores) {
        this.contextualizedTermScores = contextualizedTermScores;
    }

    public List<CriticalContext> getCriticalContexts() {
        return criticalContexts;
    }

    public void setCriticalContexts(List<CriticalContext> criticalContexts) {
        this.criticalContexts = criticalContexts;
    }

    // Legacy getters/setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.voterId = userId; // Keep in sync
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        this.ballotType = categoryId; // Keep in sync
    }

    public Term[] getTerms() {
        return terms;
    }

    public void setTerms(Term[] terms) {
        this.terms = terms;
    }

    public CompetitorDimension[] getCompetitors() {
        return competitors;
    }

    public void setCompetitors(CompetitorDimension[] competitors) {
        this.competitors = competitors;
    }

    public String[] getContexts() {
        return contexts;
    }

    public void setContexts(String[] contexts) {
        this.contexts = contexts;
    }

    @Override
    public String toString() {
        return "WorldviewBallot{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", voter='" + voterId + '\'' +
                ", contexts=" + (personalContexts != null ? personalContexts.size() : 0) +
                ", scores=" + (contextualizedTermScores != null ? contextualizedTermScores.size() : 0) +
                '}';
    }
}

