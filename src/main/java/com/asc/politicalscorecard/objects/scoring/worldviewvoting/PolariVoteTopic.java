package com.asc.politicalscorecard.objects.scoring.worldviewvoting;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A PSC-hosted public vote over candidate definitions DRAFTED in Polari
 * (a ScoreConcept per group, mechanism B, or a candidate Display,
 * mechanism A). Architecture (Dustin, 2026-07-14): Polari is the
 * drafting/analysis/aggregation layer where groups define how
 * contextualization "should be"; PSC is the locked-down, public-facing
 * layer where the actual vote on those diverging drafts happens.
 * Candidate names are snapshotted at creation so the vote stays stable
 * even if Polari's group membership changes mid-vote.
 */
public class PolariVoteTopic {
    private String id;
    private String title;
    private String description;

    // Which Polari ScoreGroup this vote concerns.
    private String polariGroupName;

    // 'concept' (mechanism B — term-weighting worldviews) or 'display'
    // (mechanism A — explanatory Displays).
    private String polariItemKind;

    // Candidate names, snapshotted from Polari at creation.
    private List<String> candidateNames;

    // 'approval' | 'sole' | 'ranked-condorcet' — mirrors Polari's
    // ELECTION_MODES vocabulary directly, no translation needed.
    private String mode;

    // OPEN accepts ballots; CLOSED stops accepting them but hasn't been
    // resolved yet; SYNCED means the result has been pushed to Polari.
    private VoteStatus status;

    private String createdBy;
    private Integer totalBallots;

    // Set once the vote is synced back to Polari.
    private String electedCandidate;
    private String electedProvenance;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum VoteStatus {
        OPEN,
        CLOSED,
        SYNCED
    }

    public PolariVoteTopic() {
        this.status = VoteStatus.OPEN;
        this.totalBallots = 0;
    }

    public PolariVoteTopic(String id, String title, String description, String polariGroupName,
                           String polariItemKind, List<String> candidateNames, String mode,
                           String createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.polariGroupName = polariGroupName;
        this.polariItemKind = polariItemKind;
        this.candidateNames = candidateNames;
        this.mode = mode;
        this.createdBy = createdBy;
        this.status = VoteStatus.OPEN;
        this.totalBallots = 0;
    }

    public boolean isOpen() {
        return status == VoteStatus.OPEN;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPolariGroupName() {
        return polariGroupName;
    }

    public void setPolariGroupName(String polariGroupName) {
        this.polariGroupName = polariGroupName;
    }

    public String getPolariItemKind() {
        return polariItemKind;
    }

    public void setPolariItemKind(String polariItemKind) {
        this.polariItemKind = polariItemKind;
    }

    public List<String> getCandidateNames() {
        return candidateNames;
    }

    public void setCandidateNames(List<String> candidateNames) {
        this.candidateNames = candidateNames;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public VoteStatus getStatus() {
        return status;
    }

    public void setStatus(VoteStatus status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getTotalBallots() {
        return totalBallots;
    }

    public void setTotalBallots(Integer totalBallots) {
        this.totalBallots = totalBallots;
    }

    public String getElectedCandidate() {
        return electedCandidate;
    }

    public void setElectedCandidate(String electedCandidate) {
        this.electedCandidate = electedCandidate;
    }

    public String getElectedProvenance() {
        return electedProvenance;
    }

    public void setElectedProvenance(String electedProvenance) {
        this.electedProvenance = electedProvenance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "PolariVoteTopic{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", polariGroupName='" + polariGroupName + '\'' +
                ", polariItemKind='" + polariItemKind + '\'' +
                ", mode='" + mode + '\'' +
                ", status=" + status +
                ", totalBallots=" + totalBallots +
                '}';
    }
}
