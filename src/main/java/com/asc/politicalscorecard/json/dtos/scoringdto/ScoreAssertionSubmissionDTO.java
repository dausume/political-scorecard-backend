package com.asc.politicalscorecard.json.dtos.scoringdto;

/**
 * A citizen's proposed ScoreAssertion (2026-07-14 general-scoring
 * follow-up) — plain request/response DTO, NOT an AbstractDTO: unlike
 * PolariVoteTopic/WorldviewVote, a submitted assertion has no PSC-side
 * persistence or local tally step (see PolariSyncService.submitScoreAssertion's
 * doc) — it's pushed straight through to Polari as one ScoreAssertion
 * row, landing in Polari's own 'asserted' status (never counted by
 * score_policy() until a human confirms it through the existing
 * asserted -> under-review -> confirmed/rejected lifecycle).
 */
public class ScoreAssertionSubmissionDTO {

    /** ScoreSubject name of the policy this claim is about. */
    private String policyName;

    /** The literal span of policy text being asserted about, if any. */
    private String quote;

    /** Plain-language statement of what this claim asserts. */
    private String intent;

    /** 'score-impact' | 'dependency' | 'decorative'. */
    private String assertionType;

    /** 'supports' | 'harms'. */
    private String direction;

    /** 0-1. */
    private Double strength;

    /** Optional — binds this claim to a specific ScoreConcept. */
    private String conceptName;

    /** Optional — binds this claim to a specific ScoreTerm. */
    private String termName;

    /** Required when assertionType='dependency' — the policy this one carries over. */
    private String dependsOnPolicy;

    /** Freetext, e.g. a source citation — until a real evidence-authoring
     *  UI exists, this is the only place a citizen can point at where
     *  their claim comes from. */
    private String notes;

    /** Set server-side after a successful submission — the Polari-side
     *  ScoreAssertion name the client can look up afterward. */
    private String assertionName;

    public ScoreAssertionSubmissionDTO() {
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getAssertionType() {
        return assertionType;
    }

    public void setAssertionType(String assertionType) {
        this.assertionType = assertionType;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Double getStrength() {
        return strength;
    }

    public void setStrength(Double strength) {
        this.strength = strength;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getDependsOnPolicy() {
        return dependsOnPolicy;
    }

    public void setDependsOnPolicy(String dependsOnPolicy) {
        this.dependsOnPolicy = dependsOnPolicy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAssertionName() {
        return assertionName;
    }

    public void setAssertionName(String assertionName) {
        this.assertionName = assertionName;
    }
}
