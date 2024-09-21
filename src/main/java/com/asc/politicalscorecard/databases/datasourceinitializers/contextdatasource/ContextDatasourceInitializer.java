package com.asc.politicalscorecard.databases.datasourceinitializers.contextdatasource;

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
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.List;
import java.util.Map;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.asc.politicalscorecard.databases.datasourceinitializers.locationsdatasource.NationInitializer;
import com.asc.politicalscorecard.databases.datasourceinitializers.locationsdatasource.PlanetInitializer;
import com.asc.politicalscorecard.databases.datasourceinitializers.locationsdatasource.StateInitializer;

import jakarta.annotation.PostConstruct;

@Component
@Scope("singleton")
public class ContextDatasourceInitializer implements EnvironmentAware {

    private final JdbcClient primaryJdbcClient;
    private final ApplicationContext applicationContext;

    private String contextDatabaseName;

    // Injects the primary JDBC client so that we can create the context database via the default mysql database.
    public ContextDatasourceInitializer(
        @Qualifier("primaryJdbcClient") JdbcClient primaryJdbcClient,
        ApplicationContext applicationContext
        ) 
        {
        this.primaryJdbcClient = primaryJdbcClient;
        this.applicationContext = applicationContext;
        initialize();
    }

    @Override
    public void setEnvironment(Environment environment) {
        // Retrieve the property value from the Environment
        this.contextDatabaseName = environment.getProperty("app.datasource.context.db_name");
    }

    @PostConstruct
    public boolean initialize() {
        try{
            createDatabaseIfNotExists();
            initializeTables();
            return true;
        } catch (Exception e) {
            System.out.println("Error in LocationDatasourceInitializer: " + e.getMessage());
            return false;
        }
    }

    private void createDatabaseIfNotExists() {
        try{
            if(contextDatabaseName == null) {
                System.out.println("Context database name is null.");
            }
            else{
                String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS " + contextDatabaseName + ";";
                System.out.println("Creating context database: " + createDatabaseSql);
                System.out.println("Primary JDBC Client: " + primaryJdbcClient);
                System.out.println("Tables shown: " + createDatabaseSql);
                // Attempt to create the database
                primaryJdbcClient.sql(createDatabaseSql).update();
                confirmDatabaseExists(contextDatabaseName);
            }
        } catch (Exception e) {
            System.out.println("Error in createDatabaseIfNotExists: " + e.getMessage());
        }
    }

    private boolean confirmDatabaseExists(String databaseName) {
        try {
            // Use Show Tables to check if the database was created
                // Execute the "SHOW TABLES" command and store the result
                // in a list of maps
                List<Map<String, Object>> tables = primaryJdbcClient.sql("SHOW DATABASES;").query().listOfRows();
                // Use AtomicBoolean to handle the mutable state in the lambda expression
                AtomicBoolean dbExists = new AtomicBoolean(false);
                // Print the tables and check if the database was created and can be seen.
                System.out.println("Tables in the database:");
                for (Map<String, Object> table : tables) {
                    table.values().forEach(dbName -> {
                        if (dbName.equals(databaseName)) {
                            System.out.println("Database already exists.");
                            dbExists.set(true);
                        }
                    });
                }
                if (dbExists.get()) {
                    return true;
                } else {
                    return false;
                }
        } catch (Exception e) {
            System.out.println("Error in confirmDatabaseExists: " + e.getMessage());
            return false;
        }
    }


    private void initializeTables() {
        // Retrieve the JdbcClient for location data source after database creation
        JdbcClient locationJdbcClient = applicationContext.getBean("locationJdbcClient", JdbcClient.class);
        
        System.out.println("In initialize tables for LocationDatasourceInitializer.");

        PlanetInitializer planetInitializer = new PlanetInitializer(locationJdbcClient);
        NationInitializer nationInitializer = new NationInitializer(locationJdbcClient);
        StateInitializer stateInitializer = new StateInitializer(locationJdbcClient);

        planetInitializer.initializeTable();
        nationInitializer.initializeTable(); // Since Nation depends on Planet, it must come after.
        stateInitializer.initializeTable();
    }

}
