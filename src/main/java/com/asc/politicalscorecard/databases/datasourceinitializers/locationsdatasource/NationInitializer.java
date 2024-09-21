package com.asc.politicalscorecard.databases.datasourceinitializers.locationsdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;

import org.springframework.beans.factory.annotation.Qualifier;


public class NationInitializer {

    private final JdbcClient locationjdbcClient;

    public NationInitializer(@Qualifier("locationJdbcClient") JdbcClient locationjdbcClient) {
        this.locationjdbcClient = locationjdbcClient;
    }

    public void initializeTable() {
        System.out.println("In Nation InitializeTable.");
        locationjdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS nation (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "nation_name VARCHAR(255) NOT NULL, " +
            "home_planet VARCHAR(255), " + // Reference to the planet object
            "FOREIGN KEY (home_planet) REFERENCES planet(id)" +
            ")"
        ).update();
    }
}
