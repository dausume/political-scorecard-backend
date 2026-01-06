package com.asc.politicalscorecard.objects.scoring.worldviewvoting;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A WorldviewElection represents a general election or polling event
 * where multiple individuals submit their WorldviewBallots.
 * Example: "2024 Presidential Election", "Q2 2024 Economic Policy Poll"
 */
public class WorldviewElection {
    // Unique identifier
    private String id;

    // Name of the election
    private String name;

    // Description/purpose of the election
    private String description;

    // Election types/categories (e.g., ["Economic Policy", "Labor Rights"])
    private List<String> electionTypes;

    // Status of the election (DRAFT, ACTIVE, CLOSED, ARCHIVED)
    private ElectionStatus status;

    // Start date/time for ballot submission
    private LocalDateTime startDate;

    // End date/time for ballot submission
    private LocalDateTime endDate;

    // ID of the user/organization that created this election
    private String createdBy;

    // List of ballot IDs associated with this election (populated when queried)
    private List<String> ballotIds;

    // Total number of ballots submitted
    private Integer totalBallots;

    // Context specifications that define what context types and granularities
    // voters must use when filling out ballots for this election
    // Example: [LOCATION:STATE, TIMEFRAME:SINGLE_YEAR]
    private List<ContextSpecification> contextSpecifications;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enum for election status
    public enum ElectionStatus {
        DRAFT,      // Being configured
        ACTIVE,     // Currently accepting ballots
        CLOSED,     // No longer accepting ballots
        ARCHIVED    // Historical record
    }

    // Constructors
    public WorldviewElection() {
        this.status = ElectionStatus.DRAFT;
        this.totalBallots = 0;
    }

    public WorldviewElection(String id, String name, String description, List<String> electionTypes,
                            ElectionStatus status, LocalDateTime startDate, LocalDateTime endDate,
                            String createdBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.electionTypes = electionTypes;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdBy = createdBy;
        this.totalBallots = 0;
    }

    // Helper methods

    /**
     * Check if the election is currently accepting ballots
     */
    public boolean isActive() {
        if (status != ElectionStatus.ACTIVE) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return (startDate == null || now.isAfter(startDate)) &&
               (endDate == null || now.isBefore(endDate));
    }

    /**
     * Check if the election has ended
     */
    public boolean hasEnded() {
        return status == ElectionStatus.CLOSED ||
               status == ElectionStatus.ARCHIVED ||
               (endDate != null && LocalDateTime.now().isAfter(endDate));
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

    public List<String> getElectionTypes() {
        return electionTypes;
    }

    public void setElectionTypes(List<String> electionTypes) {
        this.electionTypes = electionTypes;
    }

    public ElectionStatus getStatus() {
        return status;
    }

    public void setStatus(ElectionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<String> getBallotIds() {
        return ballotIds;
    }

    public void setBallotIds(List<String> ballotIds) {
        this.ballotIds = ballotIds;
    }

    public Integer getTotalBallots() {
        return totalBallots;
    }

    public void setTotalBallots(Integer totalBallots) {
        this.totalBallots = totalBallots;
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

    public List<ContextSpecification> getContextSpecifications() {
        return contextSpecifications;
    }

    public void setContextSpecifications(List<ContextSpecification> contextSpecifications) {
        this.contextSpecifications = contextSpecifications;
    }

    @Override
    public String toString() {
        return "WorldviewElection{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", types=" + electionTypes +
                ", status=" + status +
                ", totalBallots=" + totalBallots +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
