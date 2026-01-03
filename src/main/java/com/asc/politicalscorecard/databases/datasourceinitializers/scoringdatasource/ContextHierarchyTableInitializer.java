package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the ContextHierarchy table in the scoring database.
 * Stores specificity relationships between contexts (e.g., state > nation).
 */
public class ContextHierarchyTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public ContextHierarchyTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing ContextHierarchy table...");

        // Context hierarchy table for tracking specificity relationships
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS context_hierarchy (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "parent_context_id VARCHAR(255) NOT NULL, " + // Less specific context
            "child_context_id VARCHAR(255) NOT NULL, " +  // More specific context
            "context_type VARCHAR(50) NOT NULL, " +       // Type of context (LOCATION, TIMEFRAME, etc.)
            "specificity_difference INT DEFAULT 1, " +    // Levels of specificity difference
            "hierarchy_path VARCHAR(500), " +             // Full path description
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (parent_context_id) REFERENCES term_context(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (child_context_id) REFERENCES term_context(id) ON DELETE CASCADE, " +
            "UNIQUE KEY unique_hierarchy (parent_context_id, child_context_id)" +
            ")"
        ).update();

        System.out.println("ContextHierarchy table initialized successfully.");
    }
}
