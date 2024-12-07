package com.asc.politicalscorecard.databases.datasourceproperties;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

public class RedisDatasourceProperties extends RedisProperties {
    private String namespace;
    private Integer databaseIndex; // This is the database index (0-15) for isolated database properties in a given Redis instance.

    public String getNamespace() {
        return namespace;
    }

    public Integer getDatabaseIndex() {
        return databaseIndex;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setDatabaseIndex(Integer databaseIndex) {
        // Validate the database index is between `0` and `15`
        if (databaseIndex < 0 || databaseIndex > 15) {
            throw new IllegalArgumentException("Database index must be between 0 and 15.");
        }
        this.databaseIndex = databaseIndex;
    }
}