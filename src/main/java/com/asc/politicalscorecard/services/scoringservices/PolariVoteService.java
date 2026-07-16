package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.databases.daos.scoringdaos.PolariVoteTopicDAO;
import com.asc.politicalscorecard.databases.daos.scoringdaos.WorldviewVoteDAO;
import com.asc.politicalscorecard.json.dtos.scoringdto.PolariVoteTopicDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.WorldviewVoteDTO;
import com.asc.politicalscorecard.objects.scoring.worldviewvoting.PolariVoteTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer for PSC-hosted votes over candidate definitions
 * drafted in Polari (2026-07-14 ballot-hosting architecture move).
 * Orchestrates the PolariVoteTopic/WorldviewVote DAOs with
 * PolariSyncService's replay-and-apply push-back.
 */
@Service
public class PolariVoteService {

    private final PolariVoteTopicDAO topicDAO;
    private final WorldviewVoteDAO voteDAO;
    private final PolariSyncService polariSyncService;

    @Autowired
    public PolariVoteService(PolariVoteTopicDAO topicDAO, WorldviewVoteDAO voteDAO,
                             PolariSyncService polariSyncService) {
        this.topicDAO = topicDAO;
        this.voteDAO = voteDAO;
        this.polariSyncService = polariSyncService;
    }

    public ApiResponse<PolariVoteTopicDTO> createTopic(PolariVoteTopicDTO topicDTO) {
        return topicDAO.create(topicDTO);
    }

    public ApiResponse<PolariVoteTopicDTO> getTopicById(String id) {
        return topicDAO.read(id);
    }

    public ApiResponse<List<PolariVoteTopicDTO>> getAllTopics() {
        return topicDAO.readAll();
    }

    /**
     * Cast one citizen's vote. Refuses (rather than silently
     * proceeding) if the topic is not OPEN, or if the topic doesn't
     * exist. Duplicate-vote-per-voter is enforced at the DB layer
     * (UNIQUE KEY) — surfaced honestly through the DAO's ApiResponse,
     * not caught/hidden here.
     */
    public ApiResponse<WorldviewVoteDTO> castVote(String topicId, WorldviewVoteDTO voteDTO) {
        ApiResponse<PolariVoteTopicDTO> topicResponse = topicDAO.read(topicId);
        if (!topicResponse.isSuccess()) {
            return new ApiResponse<>(false, "No PolariVoteTopic found with id " + topicId, voteDTO);
        }
        PolariVoteTopicDTO topic = topicResponse.getData();
        if (!"OPEN".equals(topic.getStatus())) {
            return new ApiResponse<>(false,
                "Topic '" + topicId + "' is " + topic.getStatus() + " — only OPEN topics accept votes",
                voteDTO);
        }
        voteDTO.setTopicId(topicId);
        ApiResponse<WorldviewVoteDTO> response = voteDAO.create(voteDTO);
        if (response.isSuccess()) {
            topicDAO.incrementTotalBallots(topicId);
        }
        return response;
    }

    /**
     * Raw vote counts per candidate — a simple, honest PSC-side
     * preview (NOT the real weighted tally; ranked-condorcet's actual
     * Copeland-share math only runs in Polari once synced, per
     * "polari is where the aggregation lives"). Good enough to show
     * "who's ahead right now" without duplicating tally algorithms.
     */
    public ApiResponse<Map<String, Object>> getResults(String topicId) {
        ApiResponse<PolariVoteTopicDTO> topicResponse = topicDAO.read(topicId);
        if (!topicResponse.isSuccess()) {
            return new ApiResponse<>(false, "No PolariVoteTopic found with id " + topicId, null);
        }
        ApiResponse<List<WorldviewVoteDTO>> votesResponse = voteDAO.readByTopic(topicId);
        if (!votesResponse.isSuccess()) {
            return new ApiResponse<>(false, votesResponse.getMessage(), null);
        }
        PolariVoteTopicDTO topic = topicResponse.getData();
        List<WorldviewVoteDTO> votes = votesResponse.getData();

        Map<String, Integer> counts = new HashMap<>();
        for (String candidate : topic.getCandidateNames()) {
            counts.put(candidate, 0);
        }
        for (WorldviewVoteDTO vote : votes) {
            switch (topic.getMode()) {
                case "approval" -> {
                    if (vote.getApprovals() != null) {
                        for (String c : vote.getApprovals()) {
                            counts.merge(c, 1, Integer::sum);
                        }
                    }
                }
                case "sole" -> {
                    if (vote.getSoleChoice() != null && !vote.getSoleChoice().isEmpty()) {
                        counts.merge(vote.getSoleChoice(), 1, Integer::sum);
                    }
                }
                case "ranked-condorcet" -> {
                    // First-preference counts only — a preview, not
                    // the real Copeland tally (that's Polari's job).
                    if (vote.getRanking() != null && !vote.getRanking().isEmpty()) {
                        counts.merge(vote.getRanking().get(0), 1, Integer::sum);
                    }
                }
                default -> {
                    // Unknown mode: leave counts untouched rather than
                    // guessing which field to read.
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("topicId", topicId);
        result.put("mode", topic.getMode());
        result.put("ballotsCast", votes.size());
        result.put("counts", counts);
        result.put("note", "sole" .equals(topic.getMode())
                ? "raw vote counts — PSC-side preview only, real tally happens in Polari once synced"
                : "raw " + ("approval".equals(topic.getMode()) ? "approval" : "first-preference")
                  + " counts — PSC-side preview only, real tally happens in Polari once synced");
        return new ApiResponse<>(true, "Results computed", result);
    }

    public ApiResponse<PolariVoteTopicDTO> closeTopic(String topicId) {
        ApiResponse<PolariVoteTopicDTO> topicResponse = topicDAO.read(topicId);
        if (!topicResponse.isSuccess()) {
            return topicResponse;
        }
        PolariVoteTopicDTO topic = topicResponse.getData();
        if (!"OPEN".equals(topic.getStatus())) {
            return new ApiResponse<>(false, "Topic '" + topicId + "' is already " + topic.getStatus(), topic);
        }
        topic.setStatus(PolariVoteTopic.VoteStatus.CLOSED.name());
        return topicDAO.update(topic);
    }

    /**
     * Replays a CLOSED topic's votes into Polari and applies the
     * result — refuses on anything but CLOSED, matching the honesty
     * rule Polari's own apply_election/apply_display_vote enforce
     * (results that can still change never silently become final).
     */
    public ApiResponse<PolariVoteTopicDTO> syncToPolari(String topicId) {
        ApiResponse<PolariVoteTopicDTO> topicResponse = topicDAO.read(topicId);
        if (!topicResponse.isSuccess()) {
            return topicResponse;
        }
        PolariVoteTopicDTO topic = topicResponse.getData();
        if (!"CLOSED".equals(topic.getStatus())) {
            return new ApiResponse<>(false,
                "Topic '" + topicId + "' is " + topic.getStatus()
                        + " — only CLOSED topics can sync (close it first, an explicit action, "
                        + "so a still-changing result never silently becomes final)",
                topic);
        }
        ApiResponse<List<WorldviewVoteDTO>> votesResponse = voteDAO.readByTopic(topicId);
        if (!votesResponse.isSuccess() || votesResponse.getData().isEmpty()) {
            return new ApiResponse<>(false, "No ballots cast for topic '" + topicId + "' — nothing to sync", topic);
        }

        PolariSyncService.SyncResult syncResult = polariSyncService.syncTopicToPolari(topic, votesResponse.getData());
        if (!syncResult.ok) {
            return new ApiResponse<>(false, syncResult.error, topic);
        }

        topic.setStatus(PolariVoteTopic.VoteStatus.SYNCED.name());
        topic.setElectedCandidate(syncResult.electedCandidate);
        topic.setElectedProvenance(syncResult.electedProvenance);
        return topicDAO.update(topic);
    }
}
