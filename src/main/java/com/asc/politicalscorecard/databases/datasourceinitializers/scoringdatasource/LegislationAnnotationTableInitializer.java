package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

public class LegislationAnnotationTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public LegislationAnnotationTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing Legislation Annotations table...");

        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS legislation_annotations (" +
            "id VARCHAR(36) PRIMARY KEY, " +
            "legislation_id VARCHAR(36) NOT NULL, " +
            "user_id VARCHAR(36) NOT NULL, " +
            "group_id VARCHAR(36), " +
            "annotation_type VARCHAR(50), " +
            "body_json TEXT, " +
            "target_json TEXT, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ")"
        ).update();

        System.out.println("Legislation Annotations table initialized successfully.");
    }
}
