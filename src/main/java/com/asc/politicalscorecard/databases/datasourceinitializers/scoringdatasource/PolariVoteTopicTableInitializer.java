package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the PolariVoteTopic table — a PSC-hosted public vote
 * over candidate definitions drafted in Polari (2026-07-14 ballot-
 * hosting architecture move).
 */
public class PolariVoteTopicTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public PolariVoteTopicTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing PolariVoteTopic table...");

        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS polari_vote_topic (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "title VARCHAR(500) NOT NULL, " +
            "description TEXT, " +
            "polari_group_name VARCHAR(255) NOT NULL, " +
            "polari_item_kind VARCHAR(50) NOT NULL, " +
            "candidate_names_json TEXT, " +
            "mode VARCHAR(50) NOT NULL DEFAULT 'approval', " +
            "status VARCHAR(50) NOT NULL DEFAULT 'OPEN', " +
            "created_by VARCHAR(255), " +
            "total_ballots INT DEFAULT 0, " +
            "elected_candidate VARCHAR(255), " +
            "elected_provenance TEXT, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "INDEX idx_polari_group_name (polari_group_name), " +
            "INDEX idx_status (status)" +
            ")"
        ).update();

        System.out.println("PolariVoteTopic table initialized successfully.");
    }
}
