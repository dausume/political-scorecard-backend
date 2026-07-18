package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.json.dtos.scoringdto.PolariVoteTopicDTO;
import com.asc.politicalscorecard.json.dtos.scoringdto.WorldviewVoteDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PSC-backend to Polari server-to-server sync (2026-07-14 ballot-
 * hosting architecture move — Dustin: "polari should be where the
 * aggregation lives... a derivative based on the voting... can go
 * back to be resolved into coherent plans of execution").
 *
 * PSC hosts and tallies-locally-for-preview the actual public vote,
 * but the RESOLUTION — the real tally + writing the elected weights
 * onto the ScoreGroup — happens in Polari, reusing its already-real,
 * already-tested tally/apply machinery (worldview_elections.py /
 * group_display_vote.py) unchanged. This is a fundamentally smaller
 * trust surface than the anonymous-browser-write path flagged as a
 * blocker earlier: only PSC's own backend calls Polari's generic
 * CRUDE write endpoint, never a citizen's browser directly.
 *
 * Flow for one CLOSED PolariVoteTopic:
 *   1. Create a fresh Polari-side WorldviewElection or GroupDisplayVote
 *      row, uniquely named "psc-vote-{topicId}" — NEVER reuses an
 *      existing Polari election/vote name, so a PSC-hosted vote can
 *      never silently pollute Polari's own demo/seed data.
 *   2. Replay every PSC-cast WorldviewVote as a Polari WorldviewBallot
 *      or GroupDisplayBallot, same election/vote name, voter = PSC's
 *      own anonymized voter id.
 *   3. Call Polari's real apply endpoint — Polari computes the tally
 *      and writes the elected result.
 *   4. Report the elected candidate + provenance back to the caller,
 *      to store on the PolariVoteTopic row.
 */
@Service
public class PolariSyncService {

    private static final Logger logger = Logger.getLogger(PolariSyncService.class.getName());
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String polariApiUrl;

    public PolariSyncService(@Value("${app.polari.api-url:http://prf-backend:3000}") String polariApiUrl) {
        this.polariApiUrl = polariApiUrl;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public static class SyncResult {
        public boolean ok;
        public String error;
        public String electedCandidate;
        public String electedProvenance;
    }

    /**
     * Replays a closed topic's votes into Polari and applies the
     * result. Caller (PolariVoteService) is responsible for only
     * calling this on CLOSED topics — this method trusts that gate
     * has already been enforced, matching the honesty-rule pattern
     * Polari's own apply_election/apply_display_vote use (refuse on
     * open status), just enforced one layer up here.
     */
    public SyncResult syncTopicToPolari(PolariVoteTopicDTO topic, List<WorldviewVoteDTO> votes) {
        SyncResult result = new SyncResult();
        boolean isDisplay = "display".equals(topic.getPolariItemKind());
        String syncName = "psc-vote-" + topic.getId();

        try {
            createPolariVoteDefinition(topic, syncName, isDisplay);
            replayBallots(topic, votes, syncName, isDisplay);
            applyAndCapture(syncName, isDisplay, result);
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error syncing topic " + topic.getId() + " to Polari: ", e);
            result.ok = false;
            result.error = "Polari sync failed: " + e.getMessage();
            return result;
        }
    }

    private void createPolariVoteDefinition(PolariVoteTopicDTO topic, String syncName, boolean isDisplay)
            throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("name", syncName);
        params.put("display_name", topic.getTitle());
        params.put("description", "PSC-hosted public vote (topic " + topic.getId()
                + "), synced from political-scorecard-frontend.");
        params.put("group_name", topic.getPolariGroupName());
        params.put("mode", topic.getMode());
        params.put("status", "closed"); // PSC already closed voting before syncing.
        params.put("provenance_id", "PSC-hosted vote topic " + topic.getId());
        if (isDisplay) {
            params.put("candidate_display_names_json",
                    objectMapper.writeValueAsString(topic.getCandidateNames()));
            postCrude("GroupDisplayVote", List.of(params));
        } else {
            params.put("candidate_concept_names_json",
                    objectMapper.writeValueAsString(topic.getCandidateNames()));
            postCrude("WorldviewElection", List.of(params));
        }
    }

    private void replayBallots(PolariVoteTopicDTO topic, List<WorldviewVoteDTO> votes, String syncName,
                               boolean isDisplay) throws Exception {
        if (votes.isEmpty()) {
            throw new IllegalStateException("no ballots cast — nothing to sync");
        }
        List<Map<String, Object>> ballotParams = new ArrayList<>();
        for (WorldviewVoteDTO vote : votes) {
            Map<String, Object> params = new HashMap<>();
            params.put("name", "psc-ballot-" + vote.getId());
            params.put("voter", vote.getVoterId());
            params.put("approvals_json", objectMapper.writeValueAsString(
                    vote.getApprovals() != null ? vote.getApprovals() : new ArrayList<>()));
            params.put("sole_choice", vote.getSoleChoice() != null ? vote.getSoleChoice() : "");
            params.put("ranking_json", objectMapper.writeValueAsString(
                    vote.getRanking() != null ? vote.getRanking() : new ArrayList<>()));
            if (isDisplay) {
                params.put("vote_name", syncName);
            } else {
                params.put("election_name", syncName);
            }
            ballotParams.add(params);
        }
        postCrude(isDisplay ? "GroupDisplayBallot" : "WorldviewBallot", ballotParams);
    }

    private void applyAndCapture(String syncName, boolean isDisplay, SyncResult result) throws Exception {
        String applyPath = isDisplay
                ? "/api/scoring/display-votes/" + syncName + "/apply"
                : "/api/scoring/elections/" + syncName + "/apply";
        ResponseEntity<String> response = restTemplate.postForEntity(polariApiUrl + applyPath, null, String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        if (!body.path("ok").asBoolean(false)) {
            result.ok = false;
            result.error = body.path("error").asText("Polari apply refused (unknown reason)");
            return;
        }

        result.ok = true;
        if (isDisplay) {
            result.electedCandidate = body.path("electedDisplay").asText("");
            result.electedProvenance = body.path("electedProvenance").asText("");
        } else {
            // WorldviewElection.apply returns a weights map, not a
            // singular winner field — take the highest-weighted
            // candidate as elected, honestly labeling a tie if the
            // top weight isn't unique rather than picking arbitrarily.
            JsonNode weights = body.path("appliedWeights");
            String winner = "";
            double topWeight = -1.0;
            boolean tie = false;
            var fields = weights.fields();
            while (fields.hasNext()) {
                var entry = fields.next();
                double w = entry.getValue().asDouble();
                if (w > topWeight) {
                    topWeight = w;
                    winner = entry.getKey();
                    tie = false;
                } else if (w == topWeight) {
                    tie = true;
                }
            }
            result.electedCandidate = tie ? "" : winner;
            result.electedProvenance = body.path("weightsProvenance").asText("")
                    + (tie ? " (tied at the top weight — no single winner; resolve manually)" : "");
        }
    }

    /**
     * Creates one ScoreAssertion row in Polari, server-to-server —
     * the general-scoring "citizens submit accountable claims"
     * feature (2026-07-14 follow-up). Reuses postCrude() unchanged,
     * same trust boundary as syncTopicToPolari(): only PSC's own
     * authenticated backend calls Polari's generic CRUDE write
     * endpoint, never a citizen's browser directly. Caller
     * (ScoreAssertionSubmissionService) is responsible for forcing
     * status='asserted' and asserted_by=the resolved voter id before
     * calling this — this method trusts params as given, same as
     * postCrude() itself.
     */
    public String submitScoreAssertion(Map<String, Object> params) throws Exception {
        postCrude("ScoreAssertion", List.of(params));
        return (String) params.get("name");
    }

    /**
     * Creates one PolicyVote row in Polari, server-to-server
     * (2026-07-14 dual-path follow-up). Unlike ScoreAssertion, a
     * PolicyVote has no review lifecycle — it counts toward
     * politician_score() the instant it exists — so the caller
     * (PolicyVoteSubmissionService) is responsible for having already
     * verified the submitter holds ROLE_policy-voting-admin (with a
     * cited official source) or is an authorized member of the
     * politician's own staff group BEFORE calling this. This method
     * itself trusts params as given, same as postCrude().
     */
    public String submitPolicyVote(Map<String, Object> params) throws Exception {
        postCrude("PolicyVote", List.of(params));
        return (String) params.get("name");
    }

    /** Polari's generic auto-CRUDE create: POST /{ClassName}, body
     *  field 'initParamSets' = JSON array of __init__ kwargs objects
     *  (multipart/form-data only — Polari's CRUDE refuses JSON
     *  bodies, confirmed against the live API).
     *
     *  Built as a RAW multipart body with a fixed boundary rather than
     *  via Spring's FormHttpMessageConverter/MultipartBodyBuilder —
     *  both were tried first and both produced a body Polari's Falcon-
     *  side parser rejected as "unreadable multipart body" despite an
     *  explicit multipart/form-data Content-Type (a real, confirmed
     *  incompatibility, not a guess). This mirrors byte-for-byte what
     *  a working `curl -F` request sends (verified via
     *  `curl --trace-ascii` against the live API): one part, bare
     *  Content-Disposition, no Content-Type on the part itself, CRLF
     *  line endings, closing boundary with no trailing CRLF. */
    /** Public entry for other services that need a Polari row created
     *  through the same verified multipart CRUDE path (governance UI:
     *  LogicForkVote / LogicForkBallot / DecisionProcedureEdge). */
    public void createPolariRows(String className, List<Map<String, Object>> initParamSets) throws Exception {
        postCrude(className, initParamSets);
    }

    private void postCrude(String className, List<Map<String, Object>> initParamSets) throws Exception {
        String boundary = "----PSCPolariSync" + java.util.UUID.randomUUID();
        String json = objectMapper.writeValueAsString(initParamSets);
        String body = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"initParamSets\"\r\n"
                + "\r\n"
                + json + "\r\n"
                + "--" + boundary + "--";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("multipart/form-data; boundary=" + boundary));
        HttpEntity<byte[]> requestEntity =
                new HttpEntity<>(body.getBytes(java.nio.charset.StandardCharsets.UTF_8), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                polariApiUrl + "/" + className, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Polari refused creating " + className + ": "
                    + response.getStatusCode() + " " + response.getBody());
        }
    }
}
