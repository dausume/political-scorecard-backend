package com.asc.politicalscorecard.databases.datasourceinitializers.locationsdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;


public class PlanetInitializer {

    private final JdbcClient locationjdbcClient;

    public PlanetInitializer(@Qualifier("locationJdbcClient") JdbcClient locationjdbcClient) 
    {
        this.locationjdbcClient = locationjdbcClient;
    }

    public void initializeTable() {
             System.out.println("In State InitializeTable.");
             locationjdbcClient.sql(
            "CREATE TABLE IF NOT EXISTS planet (" +
            "id VARCHAR(255) PRIMARY KEY, " +
            "planet_name VARCHAR(255) NOT NULL" +
            ")"
            ).update();
         }
}
