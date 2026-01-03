package com.asc.politicalscorecard.databases;

import org.springframework.stereotype.Component;

@Component
public class InitializationState {

    private boolean initializedDatabases = false;
    private boolean initializedTables = false;

    // MariaDB Databases

    private boolean initializedLocationDatabase = false;
    private boolean initializedLocationTables = false;
    private String locationDatabaseName = "";

    private boolean initializedScoringDatabase = false;
    private boolean initializedScoringTables = false;
    private String scoringDatabaseName = "";

    // Redis Databases

    private boolean initializedGeoLocationDatabase = false;
    private boolean initializedGeoLocationTables = false;
    private String geoLocationDatabaseName = "";

    private boolean initializedScoringCacheDatabase = false;
    private boolean initializedScoringCacheTables = false;
    private String scoringCacheDatabaseName = "";

    // -- Database Initialization Getters

    public synchronized boolean isInitializedDatabases() {
        return initializedScoringDatabase && initializedLocationDatabase && initializedGeoLocationDatabase && initializedScoringCacheDatabase;
    }

    public synchronized boolean isInitializedLocationDatabase() {
        return initializedLocationDatabase;
    }

    public synchronized boolean isInitializedScoringDatabase() {
        return initializedScoringDatabase;
    }

    public synchronized boolean isInitializedGeoLocationDatabase() {
        return initializedGeoLocationDatabase;
    }

    public synchronized boolean isInitializedScoringCacheDatabase() {
        return initializedScoringCacheDatabase;
    }

    // -- table initialization getters

    public synchronized boolean isInitializedTables() {
        return initializedLocationTables && initializedScoringTables && initializedGeoLocationTables && initializedScoringCacheTables;
    }

    public synchronized boolean isInitializedLocationTables() {
        return initializedLocationTables;
    }

    public synchronized boolean isInitializedScoringTables() {
        return initializedScoringTables;
    }

    public synchronized boolean isInitializedGeoLocationTables() {
        return initializedGeoLocationTables;
    }

    public synchronized boolean isInitializedScoringCacheTables() {
        return initializedScoringCacheTables;
    }

    // -- Database Initialization Setters

    public synchronized void setInitializedLocationDatabase(boolean initializedLocationDatabase) {
        System.out.println("Setting initializedLocationDatabase");
        this.initializedLocationDatabase = initializedLocationDatabase;
        checkAndNotifyDatabaseInitialization();
    }

    public synchronized void setInitializedScoringDatabase(boolean initializedScoringDatabase) {
        System.out.println("Setting initializedScoringDatabase");
        this.initializedScoringDatabase = initializedScoringDatabase;
        checkAndNotifyDatabaseInitialization();
    }

    public synchronized void setInitializedGeoLocationDatabase(boolean initializedGeoLocationDatabase) {
        System.out.println("Setting initializedGeoLocationDatabase");
        this.initializedGeoLocationDatabase = initializedGeoLocationDatabase;
        checkAndNotifyDatabaseInitialization();
    }

    public synchronized void setInitializedScoringCacheDatabase(boolean initializedScoringCacheDatabase) {
        System.out.println("Setting initializedScoringCacheDatabase");
        this.initializedScoringCacheDatabase = initializedScoringCacheDatabase;
        checkAndNotifyDatabaseInitialization();
    }

    // -- Table Initialization Setters

    public synchronized void setInitializedLocationTables(boolean initializedLocationTables) {
        this.initializedLocationTables = initializedLocationTables;
        checkAndNotifyTableInitialization();
    }

    public synchronized void setInitializedScoringTables(boolean initializedScoringTables) {
        this.initializedScoringTables = initializedScoringTables;
        checkAndNotifyTableInitialization();
    }

    public synchronized void setInitializedGeoLocationTables(boolean initializedGeoLocationTables) {
        this.initializedGeoLocationTables = initializedGeoLocationTables;
        checkAndNotifyTableInitialization();
    }

    public synchronized void setInitializedScoringCacheTables(boolean initializedScoringCacheTables) {
        this.initializedScoringCacheTables = initializedScoringCacheTables;
        checkAndNotifyTableInitialization();
    }

    // -- Database Name Getters

    public synchronized String getLocationDatabaseName() {
        return locationDatabaseName;
    }

    public synchronized String getScoringDatabaseName() {
        return scoringDatabaseName;
    }

    public synchronized String getGeoLocationDatabaseName() {
        return geoLocationDatabaseName;
    }

    public synchronized String getScoringCacheDatabaseName() {
        return scoringCacheDatabaseName;
    }

    // -- Database Name Setters

    public void setLocationDatabaseName(String locationDatabaseName) {
        this.locationDatabaseName = locationDatabaseName;
    }

    public void setScoringDatabaseName(String scoringDatabaseName) {
        this.scoringDatabaseName = scoringDatabaseName;
    }

    public void setGeoLocationDatabaseName(String geoLocationDatabaseName) {
        this.geoLocationDatabaseName = geoLocationDatabaseName;
    }

    public void setScoringCacheDatabaseName(String scoringCacheDatabaseName) {
        this.scoringCacheDatabaseName = scoringCacheDatabaseName;
    }

    // -- Synchronization Methods --

    public synchronized void waitForDatabaseInitializations() throws InterruptedException {
        System.out.println("Waiting for database initializations.");
        while (!isInitializedDatabases()) {
            wait();
        }
    }

    public synchronized void waitForTableInitializations() throws InterruptedException {
        System.out.println("Waiting for table initializations.");
        while (!isInitializedTables()) {
            wait();
        }
    }

    private synchronized void checkAndNotifyDatabaseInitialization() {
        System.out.println("Checking if all databases are initialized.");
        System.out.println("Location Database: " + initializedLocationDatabase);
        System.out.println("Scoring Database: " + initializedScoringDatabase);
        System.out.println("GeoLocation Database: " + initializedGeoLocationDatabase);
        System.out.println("ScoringCache Database: " + initializedScoringCacheDatabase);
        if (isInitializedDatabases()) {
            notifyAllDatabasesReady();
        }
    }

    private synchronized void checkAndNotifyTableInitialization() {
        System.out.println("Checking if all tables are initialized.");
        System.out.println("Location Tables: " + initializedLocationTables);
        System.out.println("GeoLocation Tables: " + initializedGeoLocationTables);
        System.out.println("Scoring Tables: " + initializedScoringTables);
        System.out.println("ScoringCache Tables: " + initializedScoringCacheTables);
        if (isInitializedTables()) {
            notifyAllTablesReady();
        }
    }

    private synchronized void notifyAllDatabasesReady() {
        System.out.println("All databases initialized. Notifying waiting threads.");
        notifyAll();
    }

    private synchronized void notifyAllTablesReady() {
        System.out.println("All tables initialized. Notifying waiting threads.");
        notifyAll();
    }

}