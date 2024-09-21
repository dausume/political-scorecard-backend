package com.asc.politicalscorecard.databases.datasourceinitializers.locationsdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;


public class StateInitializer {

    private final JdbcClient jdbcClient;

    public StateInitializer(@Qualifier("locationJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void initializeTable() {
        System.out.println("In State InitializeTable.");
        jdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS state (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "state_name VARCHAR(255) NOT NULL, " +
            "parent_nation VARCHAR(255), " + // Reference to the planet object
            "FOREIGN KEY (parent_nation) REFERENCES nation(id)" +
            ")"
        ).update();
    }
}
