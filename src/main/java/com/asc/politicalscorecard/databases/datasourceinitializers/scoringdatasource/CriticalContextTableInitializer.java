package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the CriticalContext table in the scoring database.
 * Stores system-suggested important contexts for worldview ballots.
 */
public class CriticalContextTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public CriticalContextTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing CriticalContext table...");

        // Main critical context table
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS critical_context (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL, " +
            "description TEXT, " +
            "worldview_ballot_id VARCHAR(255), " + // Optional link to ballot
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")"
        ).update();

        // Junction table for critical context variations
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS critical_context_variations (" +
            "critical_context_id VARCHAR(255) NOT NULL, " +
            "context_id VARCHAR(255) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (critical_context_id, context_id), " +
            "FOREIGN KEY (critical_context_id) REFERENCES critical_context(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (context_id) REFERENCES term_context(id) ON DELETE CASCADE" +
            ")"
        ).update();

        System.out.println("CriticalContext tables initialized successfully.");
    }
}
