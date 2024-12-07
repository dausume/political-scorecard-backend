package com.asc.politicalscorecard.databases.datasourceinitializers.locationsdatasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import com.asc.politicalscorecard.databases.InitializationState;

import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

@Component
@Scope("singleton")
public class LocationDatasourceInitializer {

    private final ApplicationContext applicationContext;
    private final JdbcClient primaryJdbcClient;
    private String locationDatabaseName = "";
    private InitializationState initializationState;

    public LocationDatasourceInitializer(
        @Qualifier("primaryJdbcClient") JdbcClient primaryJdbcClient,
        ApplicationContext applicationContext,
        InitializationState initializationState
    ) {
        this.primaryJdbcClient = primaryJdbcClient;
        this.applicationContext = applicationContext;
        this.initializationState = initializationState;
        System.out.println("Location database in location db initializer : " + initializationState.getLocationDatabaseName());
        this.locationDatabaseName = initializationState.getLocationDatabaseName();
    }

    
    public void createDatabaseIfNotExists() {
        try{
            System.out.println(this.locationDatabaseName);
            if(this.locationDatabaseName == null) {
                System.out.println("Location database name is null.");
            }
            else{
                String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS " + this.locationDatabaseName + ";";
                System.out.println("Creating location database: " + createDatabaseSql);
                System.out.println("Primary JDBC Client: " + primaryJdbcClient);
                primaryJdbcClient.sql(createDatabaseSql).update();
                // Use Show Tables to check if the database was created
                confirmDatabaseExists(this.locationDatabaseName);
            }
        } catch (Exception e) {
            System.out.println("Error creating location database: " + e.getMessage());
        }
    }

    private void confirmDatabaseExists(String databaseName) {
        try {
            // Use Show Tables to check if the database was created
                // Execute the "SHOW TABLES" command and store the result
                // in a list of maps
                List<Map<String, Object>> tables = primaryJdbcClient.sql("SHOW DATABASES;").query().listOfRows();
                // Print the tables and check if the database was created and can be seen.
                System.out.println("Tables in the database:");
                for (Map<String, Object> table : tables) {
                    table.values().forEach(dbName -> {
                        if (dbName.equals(databaseName)) {
                            System.out.println("Database already exists.");
                            initializationState.setInitializedLocationDatabase(true);
                        }
                    });
                }
        } catch (Exception e) {
            System.out.println("Error in confirmDatabaseExists: " + e.getMessage());
        }
    }

    public void initialize() {
        // Retrieve the JdbcClient for location data source after database creation
        JdbcClient locationJdbcClient = applicationContext.getBean("locationJdbcClient", JdbcClient.class);
        
        System.out.println("In initialize tables for LocationDatasourceInitializer.");

        PlanetTableInitializer planetInitializer = new PlanetTableInitializer(locationJdbcClient);
        NationTableInitializer nationInitializer = new NationTableInitializer(locationJdbcClient);
        StateTableInitializer stateInitializer = new StateTableInitializer(locationJdbcClient);
        
        planetInitializer.initializeTable();
        nationInitializer.initializeTable(); // Since Nation depends on Planet, it must come after.
        stateInitializer.initializeTable();

        initializationState.setInitializedLocationTables(true);
    }
}