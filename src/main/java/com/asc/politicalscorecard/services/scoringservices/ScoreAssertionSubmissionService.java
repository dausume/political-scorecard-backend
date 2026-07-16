package com.asc.politicalscorecard.services.scoringservices;

import com.asc.politicalscorecard.controllers.responses.ApiResponse;
import com.asc.politicalscorecard.json.dtos.scoringdto.ScoreAssertionSubmissionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Citizen-facing ScoreAssertion submission (2026-07-14 general-scoring
 * follow-up — Dustin: "go ahead with 1", i.e. route creation through
 * PSC's own authenticated backend rather than a direct anonymous
 * write to Polari's still-open generic CRUDE endpoint).
 *
 * A submitted assertion lands in Polari at status='asserted' — NOT
 * 'confirmed' — regardless of what a client sends, because
 * policy_scoring.py's score_policy() only counts 'confirmed'
 * assertions by default. This is the reason ScoreAssertion is safe to
 * accept immediately from an unvetted citizen submission while
 * PolicyVote is NOT (PolicyVote has no status/review lifecycle at
 * all, so a citizen-created row would count toward a real politician's
 * score the instant it exists — deliberately NOT built here, flagged
 * to Dustin instead of silently shipped).
 */
@Service
public class ScoreAssertionSubmissionService {

    private static final Logger logger = Logger.getLogger(ScoreAssertionSubmissionService.class.getName());
    private static final List<String> ASSERTION_TYPES = List.of("score-impact", "dependency", "decorative");

    private final PolariSyncService polariSyncService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ScoreAssertionSubmissionService(PolariSyncService polariSyncService) {
        this.polariSyncService = polariSyncService;
        this.objectMapper = new ObjectMapper();
    }

    public ApiResponse<ScoreAssertionSubmissionDTO> submit(ScoreAssertionSubmissionDTO dto, String voterId) {
        if (dto.getPolicyName() == null || dto.getPolicyName().isBlank()) {
            return new ApiResponse<>(false, "policyName is required.", dto);
        }
        if (dto.getIntent() == null || dto.getIntent().isBlank()) {
            return new ApiResponse<>(false, "intent is required — say what this claim asserts.", dto);
        }

        String assertionType = ASSERTION_TYPES.contains(dto.getAssertionType())
                ? dto.getAssertionType() : "score-impact";
        String direction = "harms".equals(dto.getDirection()) ? "harms" : "supports";
        double strength = dto.getStrength() == null ? 0.5
                : Math.max(0.0, Math.min(1.0, dto.getStrength()));
        String name = "psc-assertion-" + java.util.UUID.randomUUID();

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            params.put("display_name", dto.getIntent());
            params.put("subject_name", dto.getPolicyName());
            params.put("intent", dto.getIntent());
            params.put("assertion_type", assertionType);
            if (!"decorative".equals(assertionType)) {
                params.put("direction", direction);
                params.put("strength", strength);
            }
            if (dto.getConceptName() != null && !dto.getConceptName().isBlank()) {
                params.put("concept_name", dto.getConceptName());
            }
            if (dto.getTermName() != null && !dto.getTermName().isBlank()) {
                params.put("term_name", dto.getTermName());
            }
            if ("dependency".equals(assertionType)
                    && dto.getDependsOnPolicy() != null && !dto.getDependsOnPolicy().isBlank()) {
                params.put("depends_on_subject", dto.getDependsOnPolicy());
            }
            if (dto.getQuote() != null && !dto.getQuote().isBlank()) {
                Map<String, Object> span = new HashMap<>();
                span.put("quote", dto.getQuote());
                params.put("span_json", objectMapper.writeValueAsString(span));
            }
            // status is ALWAYS 'asserted' regardless of client input —
            // a citizen submission can never land pre-confirmed, and
            // score_policy() ignores non-'confirmed' assertions by
            // default, so this has zero live scoring effect until a
            // human moves it through the real review lifecycle.
            params.put("status", "asserted");
            params.put("status_history_json", "[]");
            // Same convention as WorldviewBallot/GroupDisplayBallot
            // voter ids synced by PolariSyncService: the raw resolved
            // Keycloak subject claim, never a real name — matches
            // Dustin's "anonymized ids not real names" on both sides.
            params.put("asserted_by", voterId);
            params.put("provenance_id", "psc-citizen-submission");
            params.put("notes", dto.getNotes() == null ? "" : dto.getNotes());

            String createdName = polariSyncService.submitScoreAssertion(params);
            dto.setAssertionName(createdName);
            return new ApiResponse<>(true,
                    "Submitted for review — this claim has no scoring effect until it's confirmed.", dto);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error submitting ScoreAssertion to Polari: ", e);
            return new ApiResponse<>(false, "Polari refused the submission: " + e.getMessage(), dto);
        }
    }
}
