package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the WeightedWorldviewTerm table in the scoring database.
 * Stores terms with user-assigned weights for worldview ballots.
 */
public class WeightedWorldviewTermTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public WeightedWorldviewTermTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing WeightedWorldviewTerm table...");
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS weighted_worldview_term (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "term_id VARCHAR(255) NOT NULL, " +
            "weight DECIMAL(5, 4) NOT NULL, " + // Weight value (0.0 to 1.0)
            "worldview_ballot_id VARCHAR(255) NOT NULL, " +
            "is_positive BOOLEAN DEFAULT TRUE, " + // Whether this is a positive or negative term
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (term_id) REFERENCES term(id) ON DELETE CASCADE" +
            ")"
        ).update();

        System.out.println("WeightedWorldviewTerm table initialized successfully.");
    }
}
