package com.asc.politicalscorecard.json.dtos.scoringdto;

/**
 * A submitted PolicyVote record — one politician's vote on one policy
 * (2026-07-14 dual-path follow-up). Unlike ScoreAssertion, a PolicyVote
 * has no review/confirm lifecycle on the Polari side: the instant it
 * exists, `politician_score()` counts it. Per Dustin (verbatim):
 * "PolicyVote should either cite an official government source for the
 * vote record, and be defined by a policy voting admin, or it should be
 * defined by the Politician themselves or one of their cabinet people
 * or assistants they personally authorized to handle it." So instead
 * of a review lifecycle, this is gated by WHO can submit at all — see
 * PolicyVoteSubmissionService for the two authorization paths.
 */
public class PolicyVoteSubmissionDTO {

    /** ScoreSubject name of the politician who cast this vote. */
    private String politicianName;

    /** ScoreSubject name of the policy voted on. */
    private String policyName;

    /** 'yea' | 'nay' | 'abstain'. */
    private String vote;

    /** ISO date the vote was cast. */
    private String voteDate;

    private String chamber;
    private String session;

    /** Required on the policy-voting-admin path — a citation for the
     *  official government record this vote comes from (e.g. a state
     *  legislature roll-call URL). Optional on the politician/staff
     *  path, where the submitter's own authorized identity IS the
     *  source. */
    private String officialSourceUrl;

    private String notes;

    /** Set server-side after a successful submission. */
    private String voteName;

    /** Set server-side — 'admin' or 'staff', so the client can show
     *  which path actually authorized this submission. */
    private String authorizedVia;

    public PolicyVoteSubmissionDTO() {
    }

    public String getPoliticianName() {
        return politicianName;
    }

    public void setPoliticianName(String politicianName) {
        this.politicianName = politicianName;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getVoteDate() {
        return voteDate;
    }

    public void setVoteDate(String voteDate) {
        this.voteDate = voteDate;
    }

    public String getChamber() {
        return chamber;
    }

    public void setChamber(String chamber) {
        this.chamber = chamber;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getOfficialSourceUrl() {
        return officialSourceUrl;
    }

    public void setOfficialSourceUrl(String officialSourceUrl) {
        this.officialSourceUrl = officialSourceUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getVoteName() {
        return voteName;
    }

    public void setVoteName(String voteName) {
        this.voteName = voteName;
    }

    public String getAuthorizedVia() {
        return authorizedVia;
    }

    public void setAuthorizedVia(String authorizedVia) {
        this.authorizedVia = authorizedVia;
    }
}
