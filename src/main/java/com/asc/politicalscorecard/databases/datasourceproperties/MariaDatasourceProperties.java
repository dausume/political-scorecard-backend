package com.asc.politicalscorecard.databases.datasourceproperties;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class MariaDatasourceProperties extends DataSourceProperties {
    
    private String databaseName;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
    
}