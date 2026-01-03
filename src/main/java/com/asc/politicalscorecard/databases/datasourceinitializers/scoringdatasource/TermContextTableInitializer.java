package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the TermContext table in the scoring database.
 * Stores polymorphic context data (timeframe, location, demographic, economic, custom).
 */
public class TermContextTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public TermContextTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing TermContext table...");
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS term_context (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "type VARCHAR(50) NOT NULL, " + // TIMEFRAME, LOCATION, DEMOGRAPHIC, ECONOMIC, CUSTOM
            "label VARCHAR(255) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "INDEX idx_type (type)" +
            ")"
        ).update();

        System.out.println("TermContext table initialized successfully.");
        System.out.println("Note: Full context data with polymorphic fields will be stored in Redis scoring cache.");
    }
}
