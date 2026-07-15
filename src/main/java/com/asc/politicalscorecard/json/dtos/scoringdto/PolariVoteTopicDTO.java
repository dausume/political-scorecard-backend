package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.objects.scoring.worldviewvoting.PolariVoteTopic;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for PolariVoteTopic.
 */
public class PolariVoteTopicDTO extends AbstractDTO {

    private String title;
    private String description;
    private String polariGroupName;
    private String polariItemKind;
    private List<String> candidateNames;
    private String mode;
    private String status; // OPEN, CLOSED, SYNCED
    private String createdBy;
    private Integer totalBallots;
    private String electedCandidate;
    private String electedProvenance;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public PolariVoteTopicDTO() {
        this.totalBallots = 0;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
    public PolariVoteTopic toEntity() {
        PolariVoteTopic topic = new PolariVoteTopic();
        topic.setId(this.getId());
        topic.setTitle(this.title);
        topic.setDescription(this.description);
        topic.setPolariGroupName(this.polariGroupName);
        topic.setPolariItemKind(this.polariItemKind);
        topic.setCandidateNames(this.candidateNames);
        topic.setMode(this.mode);
        if (this.status != null) {
            topic.setStatus(PolariVoteTopic.VoteStatus.valueOf(this.status));
        }
        topic.setCreatedBy(this.createdBy);
        topic.setTotalBallots(this.totalBallots);
        topic.setElectedCandidate(this.electedCandidate);
        topic.setElectedProvenance(this.electedProvenance);
        topic.setCreatedAt(this.createdAt);
        topic.setUpdatedAt(this.updatedAt);
        return topic;
    }

    public static PolariVoteTopicDTO fromEntity(PolariVoteTopic topic) {
        if (topic == null) {
            return null;
        }
        PolariVoteTopicDTO dto = new PolariVoteTopicDTO();
        dto.setId(topic.getId());
        dto.setTitle(topic.getTitle());
        dto.setDescription(topic.getDescription());
        dto.setPolariGroupName(topic.getPolariGroupName());
        dto.setPolariItemKind(topic.getPolariItemKind());
        dto.setCandidateNames(topic.getCandidateNames());
        dto.setMode(topic.getMode());
        if (topic.getStatus() != null) {
            dto.setStatus(topic.getStatus().name());
        }
        dto.setCreatedBy(topic.getCreatedBy());
        dto.setTotalBallots(topic.getTotalBallots());
        dto.setElectedCandidate(topic.getElectedCandidate());
        dto.setElectedProvenance(topic.getElectedProvenance());
        dto.setCreatedAt(topic.getCreatedAt());
        dto.setUpdatedAt(topic.getUpdatedAt());
        return dto;
    }

    @Override
    public String toString() {
        return "PolariVoteTopicDTO{" +
                "id='" + getId() + '\'' +
                ", title='" + title + '\'' +
                ", polariGroupName='" + polariGroupName + '\'' +
                ", polariItemKind='" + polariItemKind + '\'' +
                ", mode='" + mode + '\'' +
                ", status='" + status + '\'' +
                ", totalBallots=" + totalBallots +
                '}';
    }
}
