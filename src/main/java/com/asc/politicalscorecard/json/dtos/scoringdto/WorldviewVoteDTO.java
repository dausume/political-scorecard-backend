package com.asc.politicalscorecard.json.dtos.scoringdto;

import com.asc.politicalscorecard.json.dtos.AbstractDTO;
import com.asc.politicalscorecard.objects.scoring.worldviewvoting.WorldviewVote;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for WorldviewVote (one cast ballot in one
 * PolariVoteTopic).
 */
public class WorldviewVoteDTO extends AbstractDTO {

    private String topicId;
    private String voterId;
    private List<String> approvals;
    private String soleChoice;
    private List<String> ranking;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime castAt;

    public WorldviewVoteDTO() {
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
    public WorldviewVote toEntity() {
        WorldviewVote vote = new WorldviewVote();
        vote.setId(this.getId());
        vote.setTopicId(this.topicId);
        vote.setVoterId(this.voterId);
        vote.setApprovals(this.approvals);
        vote.setSoleChoice(this.soleChoice);
        vote.setRanking(this.ranking);
        vote.setCastAt(this.castAt);
        return vote;
    }

    public static WorldviewVoteDTO fromEntity(WorldviewVote vote) {
        if (vote == null) {
            return null;
        }
        WorldviewVoteDTO dto = new WorldviewVoteDTO();
        dto.setId(vote.getId());
        dto.setTopicId(vote.getTopicId());
        dto.setVoterId(vote.getVoterId());
        dto.setApprovals(vote.getApprovals());
        dto.setSoleChoice(vote.getSoleChoice());
        dto.setRanking(vote.getRanking());
        dto.setCastAt(vote.getCastAt());
        return dto;
    }

    @Override
    public String toString() {
        return "WorldviewVoteDTO{" +
                "id='" + getId() + '\'' +
                ", topicId='" + topicId + '\'' +
                ", voterId='" + voterId + '\'' +
                ", approvals=" + approvals +
                ", soleChoice='" + soleChoice + '\'' +
                ", ranking=" + ranking +
                '}';
    }
}
