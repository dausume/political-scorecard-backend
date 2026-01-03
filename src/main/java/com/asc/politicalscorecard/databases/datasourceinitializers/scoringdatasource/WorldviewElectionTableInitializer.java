package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the WorldviewElection table in the scoring database.
 * Stores general elections/polls that contain multiple worldview ballots.
 */
public class WorldviewElectionTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public WorldviewElectionTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing WorldviewElection table...");

        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS worldview_election (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL, " +
            "description TEXT, " +
            "election_type VARCHAR(100), " +
            "status VARCHAR(50) NOT NULL DEFAULT 'DRAFT', " + // DRAFT, ACTIVE, CLOSED, ARCHIVED
            "start_date DATETIME, " +
            "end_date DATETIME, " +
            "created_by VARCHAR(255), " +
            "total_ballots INT DEFAULT 0, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "INDEX idx_status (status), " +
            "INDEX idx_election_type (election_type), " +
            "INDEX idx_created_by (created_by)" +
            ")"
        ).update();

        System.out.println("WorldviewElection table initialized successfully.");
    }
}
