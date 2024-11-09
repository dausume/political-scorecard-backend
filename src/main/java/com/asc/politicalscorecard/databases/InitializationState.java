package com.asc.politicalscorecard.databases;

import org.springframework.stereotype.Component;

@Component
public class InitializationState {

    private boolean initializedDatabases = false;
    private boolean initializedTables = false;

    // MariaDB Databases

    private boolean initializedContextDatabase = false;
    private boolean initializedContextTables = false;

    private boolean initializedLocationDatabase = false;
    private boolean initializedLocationTables = false;

    private boolean initializedScoringDatabase = false;
    private boolean initializedScoringTables = false;

    // Redis Databases

    private boolean initializedGeoLocationDatabase = false;
    private boolean initializedGeoLocationTables = false;

    // -- Getters

    public boolean isInitialized() {
        return initializedDatabases && initializedTables;
    }

    public boolean isInitializedContext() {
        return initializedContextDatabase && initializedContextTables;
    }

    public boolean isInitializedLocation() {
        return initializedLocationDatabase && initializedLocationTables;
    }

    public boolean isInitializedTables() {
        return initializedTables;
    }

    public boolean isInitializedContextDatabase() {
        return initializedContextDatabase;
    }

    public boolean isInitializedDatabases() {
        return initializedDatabases;
    }

    public boolean isInitializedLocationDatabase() {
        return initializedLocationDatabase;
    }

    public boolean isInitializedLocationTables() {
        return initializedLocationTables;
    }

    public boolean isInitializedContextTables() {
        return initializedContextTables;
    }

    public boolean isInitializedScoringDatabase() {
        return initializedScoringDatabase;
    }

    public boolean isInitializedScoringTables() {
        return initializedScoringTables;
    }

    public boolean isInitializedGeoLocationDatabase() {
        return initializedGeoLocationDatabase;
    }

    public boolean isInitializedGeoLocationTables() {
        return initializedGeoLocationTables;
    }

    // -- Setters

    public void setInitializedDatabases(boolean initializedDatabases) {
        this.initializedDatabases = initializedDatabases;
    }

    public void setInitializedTables(boolean initializedTables) {
        this.initializedTables = initializedTables;
    }

    public void setInitializedContextDatabase(boolean initializedContextDatabase) {
        this.initializedContextDatabase = initializedContextDatabase;
    }

    public void setInitializedContextTables(boolean initializedContextTables) {
        this.initializedContextTables = initializedContextTables;
    }

    public void setInitializedLocationDatabase(boolean initializedLocationDatabase) {
        this.initializedLocationDatabase = initializedLocationDatabase;
    }

    public void setInitializedLocationTables(boolean initializedLocationTables) {
        this.initializedLocationTables = initializedLocationTables;
    }

    public void setInitializedScoringDatabase(boolean initializedScoringDatabase) {
        this.initializedScoringDatabase = initializedScoringDatabase;
    }

    public void setInitializedScoringTables(boolean initializedScoringTables) {
        this.initializedScoringTables = initializedScoringTables;
    }

    public void setInitializedGeoLocationDatabase(boolean initializedGeoLocationDatabase) {
        this.initializedGeoLocationDatabase = initializedGeoLocationDatabase;
    }

    public void setInitializedGeoLocationTables(boolean initializedGeoLocationTables) {
        this.initializedGeoLocationTables = initializedGeoLocationTables;
    }

}