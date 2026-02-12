package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

public class LegislationTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public LegislationTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing Legislation table...");

        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS legislation (" +
            "id VARCHAR(36) PRIMARY KEY, " +
            "title VARCHAR(500) NOT NULL, " +
            "description TEXT, " +
            "legislative_body_id VARCHAR(36), " +
            "valid_from_date VARCHAR(50), " +
            "valid_to_date VARCHAR(50), " +
            "legislation_text LONGTEXT, " +
            "url VARCHAR(2000), " +
            "status VARCHAR(20) DEFAULT 'DRAFT', " +
            "created_by VARCHAR(36), " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")"
        ).update();

        System.out.println("Legislation table initialized successfully.");
    }
}
