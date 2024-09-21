package com.asc.politicalscorecard.databases.datasourceinitializers.locationsdatasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

import jakarta.annotation.PostConstruct;

@Component
@Scope("singleton")
public class LocationDatasourceInitializer implements EnvironmentAware {

    private final ApplicationContext applicationContext;
    private final JdbcClient primaryJdbcClient;
    private String locationDatabaseName;

    public LocationDatasourceInitializer(
        @Qualifier("primaryJdbcClient") JdbcClient primaryJdbcClient,
        ApplicationContext applicationContext
    ) {
        this.primaryJdbcClient = primaryJdbcClient;
        this.applicationContext = applicationContext;
        initialize();
    }

    @Override
    public void setEnvironment(Environment environment) {
        // Retrieve the property value from the Environment
        this.locationDatabaseName = environment.getProperty("app.datasource.location.db_name");
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
            if(locationDatabaseName == null) {
                System.out.println("Location database name is null.");
            }
            else{
                String createDatabaseSql = "CREATE DATABASE IF NOT EXISTS " + this.locationDatabaseName + ";";
                System.out.println("Creating location database: " + createDatabaseSql);
                System.out.println("Primary JDBC Client: " + primaryJdbcClient);
                primaryJdbcClient.sql(createDatabaseSql).update();
                // Use Show Tables to check if the database was created
                primaryJdbcClient.sql("SHOW Databases;");
                System.out.println("Tables shown: " + createDatabaseSql);
            }
        } catch (Exception e) {
            System.out.println("Error creating location database: " + e.getMessage());
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
