package com.asc.politicalscorecard.objects.scoring.worldviewvoting;

import java.time.LocalDateTime;
import java.util.List;

/**
 * One citizen's cast vote in one PolariVoteTopic. Redefined 2026-07-14
 * (was an unimplemented, zero-reference stub — no persistence layer,
 * no getters/setters — safe to redefine cleanly) as part of the
 * Democratic Scorecard revamp's ballot-hosting move to PSC.
 *
 * `voterId` is PSC's own anonymized user id (the Keycloak `sub` claim
 * via UserInfoService — an opaque identifier, never a real name),
 * matching Dustin's "anonymized usernames or ids not real names" on
 * both sides of the PSC/Polari split.
 *
 * Which payload field counts depends on the topic's mode — same
 * mode-dependent-field convention Polari's own WorldviewBallot uses.
 */
public class WorldviewVote {
    private String id;
    private String topicId;
    private String voterId;

    // approval mode: candidate names this voter approves of.
    private List<String> approvals;

    // sole mode: exactly one candidate name.
    private String soleChoice;

    // ranked-condorcet mode: candidate names, most-preferred first.
    private List<String> ranking;

    private LocalDateTime castAt;

    public WorldviewVote() {
    }

    public WorldviewVote(String id, String topicId, String voterId, List<String> approvals,
                         String soleChoice, List<String> ranking) {
        this.id = id;
        this.topicId = topicId;
        this.voterId = voterId;
        this.approvals = approvals;
        this.soleChoice = soleChoice;
        this.ranking = ranking;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public List<String> getApprovals() {
        return approvals;
    }

    public void setApprovals(List<String> approvals) {
        this.approvals = approvals;
    }

    public String getSoleChoice() {
        return soleChoice;
    }

    public void setSoleChoice(String soleChoice) {
        this.soleChoice = soleChoice;
    }

    public List<String> getRanking() {
        return ranking;
    }

    public void setRanking(List<String> ranking) {
        this.ranking = ranking;
    }

    public LocalDateTime getCastAt() {
        return castAt;
    }

    public void setCastAt(LocalDateTime castAt) {
        this.castAt = castAt;
    }

    @Override
    public String toString() {
        return "WorldviewVote{" +
                "id='" + id + '\'' +
                ", topicId='" + topicId + '\'' +
                ", voterId='" + voterId + '\'' +
                ", approvals=" + approvals +
                ", soleChoice='" + soleChoice + '\'' +
                ", ranking=" + ranking +
                ", castAt=" + castAt +
                '}';
    }
}
