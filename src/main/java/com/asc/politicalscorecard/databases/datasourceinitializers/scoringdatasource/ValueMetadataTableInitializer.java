package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the ValueMetadata table in the scoring database.
 * Stores metadata about term values (type, unit, label, polarity).
 */
public class ValueMetadataTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public ValueMetadataTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing ValueMetadata table...");
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS value_metadata (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "value_type VARCHAR(50) NOT NULL, " + // PERCENTAGE, CURRENCY, COUNT, etc.
            "unit VARCHAR(50), " + // e.g., "$", "%", "people"
            "label VARCHAR(255), " + // Display label
            "is_positive BOOLEAN DEFAULT TRUE, " + // Whether higher values are better
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")"
        ).update();

        System.out.println("ValueMetadata table initialized successfully.");
    }
}
