package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the WorldviewBallot table in the scoring database.
 * Stores user worldview ballots with personal contexts and term scores.
 */
public class WorldviewBallotTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public WorldviewBallotTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing WorldviewBallot table...");

        // Main worldview ballot table
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS worldview_ballot (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "election_id VARCHAR(255), " + // Link to worldview election
            "competitive_score_id VARCHAR(255), " +
            "voter_id VARCHAR(255) NOT NULL, " +
            "name VARCHAR(255) NOT NULL, " +
            "ballot_type VARCHAR(100), " +
            // Legacy fields for backward compatibility
            "user_id VARCHAR(255), " + // Alias for voter_id
            "category_id VARCHAR(100), " + // Alias for ballot_type
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "INDEX idx_election_id (election_id), " +
            "INDEX idx_voter_id (voter_id), " +
            "FOREIGN KEY (election_id) REFERENCES worldview_election(id) ON DELETE CASCADE" +
            ")"
        ).update();

        // Junction table for personal contexts
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS worldview_ballot_personal_contexts (" +
            "ballot_id VARCHAR(255) NOT NULL, " +
            "context_id VARCHAR(255) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (ballot_id, context_id), " +
            "FOREIGN KEY (ballot_id) REFERENCES worldview_ballot(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (context_id) REFERENCES term_context(id) ON DELETE CASCADE" +
            ")"
        ).update();

        // Junction table for contextualized term scores
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS worldview_ballot_scores (" +
            "ballot_id VARCHAR(255) NOT NULL, " +
            "score_id VARCHAR(255) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (ballot_id, score_id), " +
            "FOREIGN KEY (ballot_id) REFERENCES worldview_ballot(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (score_id) REFERENCES contextualized_term_score(id) ON DELETE CASCADE" +
            ")"
        ).update();

        // Junction table for critical contexts
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS worldview_ballot_critical_contexts (" +
            "ballot_id VARCHAR(255) NOT NULL, " +
            "critical_context_id VARCHAR(255) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (ballot_id, critical_context_id), " +
            "FOREIGN KEY (ballot_id) REFERENCES worldview_ballot(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (critical_context_id) REFERENCES critical_context(id) ON DELETE CASCADE" +
            ")"
        ).update();

        // Junction table for legacy terms (backward compatibility)
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS worldview_ballot_legacy_terms (" +
            "ballot_id VARCHAR(255) NOT NULL, " +
            "term_id VARCHAR(255) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (ballot_id, term_id), " +
            "FOREIGN KEY (ballot_id) REFERENCES worldview_ballot(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (term_id) REFERENCES term(id) ON DELETE CASCADE" +
            ")"
        ).update();

        // Junction table for legacy contexts (backward compatibility)
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS worldview_ballot_legacy_contexts (" +
            "ballot_id VARCHAR(255) NOT NULL, " +
            "context_id VARCHAR(255) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (ballot_id, context_id), " +
            "FOREIGN KEY (ballot_id) REFERENCES worldview_ballot(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (context_id) REFERENCES term_context(id) ON DELETE CASCADE" +
            ")"
        ).update();

        System.out.println("WorldviewBallot tables initialized successfully.");
    }
}
