package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the Term table in the scoring database.
 * Stores abstract terms used in scoring calculations.
 */
public class TermTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public TermTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing Term table...");

        // Main term table
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS term (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL, " +
            "description TEXT, " +
            "source TEXT, " +
            "category VARCHAR(100), " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")"
        ).update();

        // Junction table for logical equivalence relationships
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS term_logical_equivalence (" +
            "term_id VARCHAR(255) NOT NULL, " +
            "equivalent_term_id VARCHAR(255) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (term_id, equivalent_term_id), " +
            "FOREIGN KEY (term_id) REFERENCES term(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (equivalent_term_id) REFERENCES term(id) ON DELETE CASCADE" +
            ")"
        ).update();

        // Junction table for competitive term relationships
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS term_competitive (" +
            "term_id VARCHAR(255) NOT NULL, " +
            "competitive_term_id VARCHAR(255) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (term_id, competitive_term_id), " +
            "FOREIGN KEY (term_id) REFERENCES term(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (competitive_term_id) REFERENCES term(id) ON DELETE CASCADE" +
            ")"
        ).update();

        System.out.println("Term tables initialized successfully.");
    }
}
