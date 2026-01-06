package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the DebateMessage table in the scoring database.
 * Stores debate/discussion messages tied to specific WorldviewElections.
 */
public class DebateMessageTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public DebateMessageTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing DebateMessage table...");

        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS debate_message (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "election_id VARCHAR(255) NOT NULL, " +
            "user_id VARCHAR(255) NOT NULL, " +
            "username VARCHAR(255) NOT NULL, " +
            "message TEXT NOT NULL, " +
            "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "edited BOOLEAN DEFAULT FALSE, " +
            "edited_at TIMESTAMP NULL, " +
            "deleted BOOLEAN DEFAULT FALSE, " +
            "deleted_at TIMESTAMP NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "INDEX idx_election_id (election_id), " +
            "INDEX idx_user_id (user_id), " +
            "INDEX idx_timestamp (timestamp), " +
            "FOREIGN KEY (election_id) REFERENCES worldview_election(id) ON DELETE CASCADE" +
            ")"
        ).update();

        System.out.println("DebateMessage table initialized successfully.");
    }
}
