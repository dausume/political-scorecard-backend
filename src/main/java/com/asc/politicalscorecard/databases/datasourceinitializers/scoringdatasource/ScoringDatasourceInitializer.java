package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.asc.politicalscorecard.databases.InitializationState;

import jakarta.annotation.PostConstruct;

@Component
@Scope("singleton")
public class ScoringDatasourceInitializer {

    private final JdbcClient primaryJdbcClient;
    private final ApplicationContext applicationContext;
    private InitializationState initializationState;

    private String scoringDatasourceName = "";

    // Injects the primary JDBC client so that we can create the context database via the default mysql database.
    public ScoringDatasourceInitializer(
        @Qualifier("primaryJdbcClient") JdbcClient primaryJdbcClient,
        ApplicationContext applicationContext,
        InitializationState initializationState
        ) 
        {
        this.primaryJdbcClient = primaryJdbcClient;
        this.applicationContext = applicationContext;
        this.initializationState = initializationState;
        this.scoringDatasourceName = initializationState.getScoringDatabaseName();
        System.out.println("Scoring Database Name: " + scoringDatasourceName);
    }

    
    public void createDatabaseIfNotExists() {
        System.out.println("In createDatabaseIfNotExists for ScoringDatasourceInitializer.");
        try{
            if(scoringDatasourceName == null) {
                System.out.println("Scoring database name is null.");
            }
            else{
                String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS " + scoringDatasourceName + ";";
                System.out.println("Creating scoring database: " + createDatabaseSql);
                System.out.println("Primary JDBC Client: " + primaryJdbcClient);
                // Attempt to create the database
                primaryJdbcClient.sql(createDatabaseSql).update();
                confirmDatabaseExists(scoringDatasourceName);
            }
        } catch (Exception e) {
            System.out.println("Error in createDatabaseIfNotExists: " + e.getMessage());
        }
    }

    private void confirmDatabaseExists(String databaseName) {
        try {
            // Use Show Tables to check if the database was created
                // Execute the "SHOW TABLES" command and store the result
                // in a list of maps
                List<Map<String, Object>> tables = primaryJdbcClient.sql("SHOW DATABASES;").query().listOfRows();
                // Use AtomicBoolean to handle the mutable state in the lambda expression
                // Print the tables and check if the database was created and can be seen.
                System.out.println("Tables in the database:");
                for (Map<String, Object> table : tables) {
                    table.values().forEach(dbName -> {
                        if (dbName.equals(databaseName)) {
                            System.out.println("Database already exists.");
                            this.initializationState.setInitializedScoringDatabase(true);
                        }
                    });
                }
        } catch (Exception e) {
            System.out.println("Error in confirmDatabaseExists: " + e.getMessage());
        }
    }


    public void initialize() {
        // Retrieve the JdbcClient for location data source after database creation
        JdbcClient scoringJdbcClient = applicationContext.getBean("scoringJdbcClient", JdbcClient.class);
        
        System.out.println("In initialize tables for LocationDatasourceInitializer.");

        //ContextTableInitializer contextTableInitializer = new ContextTableInitializer(scoringJdbcClient);

        initializationState.setInitializedScoringTables(true);
    }

}
