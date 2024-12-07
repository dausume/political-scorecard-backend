package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class ContextTableInitializer {

    private final JdbcClient scoringJdbcClient;

    // Injects the primary JDBC client so that we can create the context database via the default mysql database.
    public ContextTableInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient
    
    ) {
        this.scoringJdbcClient = scoringJdbcClient;
        initialize();
    }

    // This method initializes the tables and inserts data
    public boolean initialize() {
        try {
            createTable();
            insertInitialData();
            return true;
        } catch (Exception e) {
            System.out.println("Error in ScoringDatasourceInitializer: " + e.getMessage());
            return false;
        }
    }

    private void createTable() {
        scoringJdbcClient.sql("CREATE TABLE IF NOT EXISTS context ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "name VARCHAR(100)) ").update();
    }

    private void insertInitialData() {
        scoringJdbcClient.sql("INSERT INTO context (name) VALUES ('Context 1')");
        scoringJdbcClient.sql("INSERT INTO context (name) VALUES ('Context 2')");
    }
}