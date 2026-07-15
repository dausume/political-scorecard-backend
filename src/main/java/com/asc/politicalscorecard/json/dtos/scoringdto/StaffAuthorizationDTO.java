package com.asc.politicalscorecard.json.dtos.scoringdto;

/**
 * A policy-voting-admin's request to authorize a Keycloak user as a
 * politician's staff member for PolicyVote submission purposes
 * (2026-07-14 — see PolicyVoteSubmissionService's doc). Adds the
 * target user to a per-politician Keycloak group; does not itself
 * verify the real-world relationship — that judgment call belongs to
 * whoever holds the policy-voting-admin role.
 */
public class StaffAuthorizationDTO {

    /** ScoreSubject name of the politician being represented. */
    private String politicianName;

    /** Exact Keycloak username of the person being authorized. */
    private String username;

    public StaffAuthorizationDTO() {
    }

    public String getPoliticianName() {
        return politicianName;
    }

    public void setPoliticianName(String politicianName) {
        this.politicianName = politicianName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
