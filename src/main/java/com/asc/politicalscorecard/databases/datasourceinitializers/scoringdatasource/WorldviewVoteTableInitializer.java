package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the WorldviewVote table — one citizen's cast vote in
 * one PolariVoteTopic (2026-07-14 ballot-hosting architecture move;
 * WorldviewVote was previously an unimplemented, unpersisted stub).
 */
public class WorldviewVoteTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public WorldviewVoteTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing WorldviewVote table...");

        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS worldview_vote (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "topic_id VARCHAR(255) NOT NULL, " +
            "voter_id VARCHAR(255) NOT NULL, " +
            "approvals_json TEXT, " +
            "sole_choice VARCHAR(255), " +
            "ranking_json TEXT, " +
            "cast_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "INDEX idx_topic_id (topic_id), " +
            "INDEX idx_voter_id (voter_id), " +
            // One vote per voter per topic — recasting requires an
            // explicit update, never a silent duplicate.
            "UNIQUE KEY uq_topic_voter (topic_id, voter_id), " +
            "FOREIGN KEY (topic_id) REFERENCES polari_vote_topic(id) ON DELETE CASCADE" +
            ")"
        ).update();

        System.out.println("WorldviewVote table initialized successfully.");
    }
}
