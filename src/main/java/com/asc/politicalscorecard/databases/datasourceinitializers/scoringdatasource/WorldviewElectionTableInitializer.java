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
            "election_types JSON, " +
            "status VARCHAR(50) NOT NULL DEFAULT 'DRAFT', " + // DRAFT, ACTIVE, CLOSED, ARCHIVED
            "start_date DATETIME, " +
            "end_date DATETIME, " +
            "created_by VARCHAR(255), " +
            "total_ballots INT DEFAULT 0, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "INDEX idx_status (status), " +
            "INDEX idx_created_by (created_by)" +
            ")"
        ).update();

        System.out.println("WorldviewElection table initialized successfully.");

        // Insert sample elections for testing/development
        insertSampleElections();
    }

    private void insertSampleElections() {
        System.out.println("Checking for sample elections...");

        // Check if election-1 exists
        Integer count1 = scoringJdbcClient.sql("SELECT COUNT(*) FROM worldview_election WHERE id = ?")
            .param("election-1")
            .query(Integer.class)
            .single();

        if (count1 == 0) {
            System.out.println("Inserting sample election: election-1");
            scoringJdbcClient.sql(
                "INSERT INTO worldview_election (id, name, description, election_types, status, start_date, end_date, created_by, total_ballots) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            )
            .param("election-1")
            .param("Labor Quality Worldview Election 2024")
            .param("Democratic process to establish the meaning and measurement of labor quality across US states")
            .param("[\"WORLDVIEW\", \"POLICY_EVALUATION\"]")
            .param("ACTIVE")
            .param("2024-01-01")
            .param("2024-12-31")
            .param("system")
            .param(0)
            .update();
            System.out.println("Sample election-1 inserted successfully.");
        } else {
            System.out.println("Sample election-1 already exists.");
        }

        // Check if election-2 exists
        Integer count2 = scoringJdbcClient.sql("SELECT COUNT(*) FROM worldview_election WHERE id = ?")
            .param("election-2")
            .query(Integer.class)
            .single();

        if (count2 == 0) {
            System.out.println("Inserting sample election: election-2");
            scoringJdbcClient.sql(
                "INSERT INTO worldview_election (id, name, description, election_types, status, start_date, end_date, created_by, total_ballots) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            )
            .param("election-2")
            .param("Economic Policy Assessment 2024")
            .param("Democratic evaluation of economic policy approaches and their real-world impacts")
            .param("[\"WORLDVIEW\", \"ECONOMIC_POLICY\"]")
            .param("ACTIVE")
            .param("2024-01-01")
            .param("2024-12-31")
            .param("system")
            .param(0)
            .update();
            System.out.println("Sample election-2 inserted successfully.");
        } else {
            System.out.println("Sample election-2 already exists.");
        }
    }
}
