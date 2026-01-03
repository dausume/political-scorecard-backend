package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the ContextualizedTermScore table in the scoring database.
 * Links contextualized terms to their weights in worldview ballots.
 */
public class ContextualizedTermScoreTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public ContextualizedTermScoreTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing ContextualizedTermScore table...");
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS contextualized_term_score (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "contextualized_term_id VARCHAR(255) NOT NULL, " +
            "weight DECIMAL(5, 4) NOT NULL, " + // Weight value (0.0 to 1.0)
            "worldview_ballot_id VARCHAR(255) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (contextualized_term_id) REFERENCES contextualized_term(id) ON DELETE CASCADE" +
            ")"
        ).update();

        System.out.println("ContextualizedTermScore table initialized successfully.");
    }
}
