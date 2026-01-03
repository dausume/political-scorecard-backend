package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the ContextualizedTerm table in the scoring database.
 * Stores terms with applied contexts and normalization values.
 */
public class ContextualizedTermTableInitializer {

    private final JdbcClient scoringJdbcClient;

    public ContextualizedTermTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeTable() {
        System.out.println("Initializing ContextualizedTerm table...");

        // Main contextualized term table
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS contextualized_term (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "term_id VARCHAR(255) NOT NULL, " +
            "value_metadata_id VARCHAR(255), " +
            "pre_normalized_value DECIMAL(15, 4), " +
            "post_normalized_value DECIMAL(15, 4), " +
            "pre_process_polari_url TEXT, " + // Future use
            "post_process_polari_url TEXT, " + // Future use
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (term_id) REFERENCES term(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (value_metadata_id) REFERENCES value_metadata(id) ON DELETE SET NULL" +
            ")"
        ).update();

        // Junction table linking contextualized terms to contexts
        scoringJdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS contextualized_term_contexts (" +
            "contextualized_term_id VARCHAR(255) NOT NULL, " +
            "context_id VARCHAR(255) NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (contextualized_term_id, context_id), " +
            "FOREIGN KEY (contextualized_term_id) REFERENCES contextualized_term(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (context_id) REFERENCES term_context(id) ON DELETE CASCADE" +
            ")"
        ).update();

        System.out.println("ContextualizedTerm tables initialized successfully.");
    }
}
