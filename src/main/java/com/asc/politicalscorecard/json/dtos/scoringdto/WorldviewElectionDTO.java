package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.objects.scoring.worldviewvoting.WorldviewElection;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for WorldviewElection.
 * Used for API communication and data transfer.
 */
public class WorldviewElectionDTO extends AbstractDTO {

    private String name;
    private String description;
    private List<String> electionTypes;
    private String status; // DRAFT, ACTIVE, CLOSED, ARCHIVED

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    private String createdBy;
    private List<String> ballotIds;
    private Integer totalBallots;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public WorldviewElectionDTO() {
        this.totalBallots = 0;
    }

    public WorldviewElectionDTO(String id, String name, String description, List<String> electionTypes,
                               String status, LocalDateTime startDate, LocalDateTime endDate,
                               String createdBy, Integer totalBallots) {
        this.setId(id);
        this.name = name;
        this.description = description;
        this.electionTypes = electionTypes;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdBy = createdBy;
        this.totalBallots = totalBallots != null ? totalBallots : 0;
    }

    // Getters and Setters
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    // Convert DTO to Entity
    @Override
    public WorldviewElection toEntity() {
        WorldviewElection election = new WorldviewElection();
        election.setId(this.getId());
        election.setName(this.name);
        election.setDescription(this.description);
        election.setElectionTypes(this.electionTypes);

        // Convert status string to enum
        if (this.status != null) {
            election.setStatus(WorldviewElection.ElectionStatus.valueOf(this.status));
        }

        election.setStartDate(this.startDate);
        election.setEndDate(this.endDate);
        election.setCreatedBy(this.createdBy);
        election.setBallotIds(this.ballotIds);
        election.setTotalBallots(this.totalBallots);
        election.setCreatedAt(this.createdAt);
        election.setUpdatedAt(this.updatedAt);

        return election;
    }

    // Convert Entity to DTO
    public static WorldviewElectionDTO fromEntity(WorldviewElection election) {
        if (election == null) {
            return null;
        }
        WorldviewElectionDTO dto = new WorldviewElectionDTO();
        dto.setId(election.getId());
        dto.setName(election.getName());
        dto.setDescription(election.getDescription());
        dto.setElectionTypes(election.getElectionTypes());

        // Convert enum to string
        if (election.getStatus() != null) {
            dto.setStatus(election.getStatus().name());
        }

        dto.setStartDate(election.getStartDate());
        dto.setEndDate(election.getEndDate());
        dto.setCreatedBy(election.getCreatedBy());
        dto.setBallotIds(election.getBallotIds());
        dto.setTotalBallots(election.getTotalBallots());
        dto.setCreatedAt(election.getCreatedAt());
        dto.setUpdatedAt(election.getUpdatedAt());

        return dto;
    }

    @Override
    public String toString() {
        return "WorldviewElectionDTO{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", electionTypes=" + electionTypes +
                ", status='" + status + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", createdBy='" + createdBy + '\'' +
                ", totalBallots=" + totalBallots +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
